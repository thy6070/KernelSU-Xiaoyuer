// fpsbind: 通过 netlink proc connector 监听进程/线程创建，
// 对匹配规则的进程立即设置 CPU 亲和性（毫秒级，替代 shell 轮询）
// 编译：aarch64-linux-android-clang -O2 -static fpsbind.c -o fpsbind
// 用法：fpsbind <mask_hex> [包名1 包名2 ...]
//   mask_hex：目标核掩码，如 0xF0 表示 cpu4-7
//   包名：匹配 /proc/<pid>/comm 前缀（游戏进程名通常等于包名）

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <signal.h>
#include <sched.h>
#include <dirent.h>
#include <sys/socket.h>
#include <linux/netlink.h>
#include <linux/connector.h>
#include <linux/cn_proc.h>

static unsigned long g_mask = 0;      // 大核（H）
static unsigned long g_all_mask = 0;  // 全部核（动态读）
static unsigned long g_small_mask = 0; // 小核（e-core）
static unsigned long g_mid_mask = 0;   // 中核（p-core）
static unsigned char g_bound[65536];   // tid 已绑标记（只绑一次，避免反复迁移卡顿）
static char g_pkgs[64][128];
static int g_npkgs = 0;
static volatile int g_run = 1;

static void on_sig(int sig) { (void)sig; g_run = 0; }

// 读 /proc/<pid>/comm
static int read_comm(pid_t pid, char *buf, size_t len) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/comm", (int)pid);
    int fd = open(path, O_RDONLY);
    if (fd < 0) return -1;
    ssize_t n = read(fd, buf, len - 1);
    close(fd);
    if (n <= 0) return -1;
    buf[n] = 0;
    // 去掉换行
    for (int i = 0; i < n; i++) if (buf[i] == '\n') { buf[i] = 0; break; }
    return 0;
}

static int pkg_match(const char *comm) {
    for (int i = 0; i < g_npkgs; i++) {
        if (strncmp(comm, g_pkgs[i], strlen(g_pkgs[i])) == 0) return 1;
    }
    return 0;
}

// 对一个 pid 的所有线程设置亲和性
static void bind_pid(pid_t pid) {
    char dirpath[64], tpath[80], tidstr[32], comm[128];
    snprintf(dirpath, sizeof(dirpath), "/proc/%d/task", (int)pid);
    DIR *d = opendir(dirpath);
    if (!d) return;
    struct dirent *de;
    cpu_set_t set;
    int nthreads = 0;
    read_comm(pid, comm, sizeof(comm));
    while ((de = readdir(d))) {
        if (de->d_name[0] == '.') continue;
        int tid = atoi(de->d_name);
        if (tid <= 0 || tid >= 65536) continue;
        if (g_bound[tid]) continue;   // 只绑一次（避免每 100ms 反复迁移线程导致卡顿）
        snprintf(tpath, sizeof(tpath), "/proc/%d/task/%d", (int)pid, tid);
        int fd = open(tpath, O_RDONLY);
        if (fd >= 0) close(fd);  // 仅确认存活
        // 精细：渲染/主线程→大核，音频/网络/IO→小核（若无小核信息则全核），其他→全核
        char tcomm[64];
        unsigned long use = 0;  // 0 = 用 g_mask
        if (read_comm(tid, tcomm, sizeof(tcomm)) == 0) {
            // 按线程类型分核（AppOpt 精细规则）
            int is_render = (strstr(tcomm, "Render") || strstr(tcomm, "render") ||
                             strstr(tcomm, "GL") || strstr(tcomm, "gfx") ||
                             strstr(tcomm, "Graphics") || strstr(tcomm, "vk") ||
                             strstr(tcomm, "Unity") || strstr(tcomm, "Unreal") ||
                             strcmp(tcomm, "main") == 0 || strcmp(tcomm, "Main") == 0);
            int is_low = (strstr(tcomm, "Audio") || strstr(tcomm, "audio") ||
                          strstr(tcomm, "Net") || strstr(tcomm, "net") ||
                          strstr(tcomm, "binder") || strstr(tcomm, "io") ||
                          strstr(tcomm, "IO") || strstr(tcomm, "file") ||
                          strstr(tcomm, "log") || strstr(tcomm, "Worker"));
            if (is_render) use = g_mask | g_mid_mask;              // 渲染 → 大核+中核（多核，避免单核卡）
            else if (is_low && g_small_mask) use = g_small_mask;  // 音频/网络/IO → 小核
            else if (g_mid_mask) use = g_mid_mask;                // 其他 → 中核
            else use = g_all_mask & ~g_mask;
            fprintf(stderr, "  tid=%d comm=%s type=%s use=0x%lx\n",
                    tid, tcomm, is_render ? "BIG" : (is_low ? "SMALL" : "MID"), use);
        }
        CPU_ZERO(&set);
        if (use == 0) use = g_mask;
        for (int c = 0; c < 32; c++) if (use & (1UL << c)) CPU_SET(c, &set);
        if (sched_setaffinity(tid, sizeof(set), &set) == 0) { nthreads++; g_bound[tid] = 1; }
    }
    closedir(d);
    fprintf(stderr, "bind pid=%d comm=%s threads=%d big=0x%lx mid=0x%lx small=0x%lx\n", (int)pid, comm, nthreads, g_mask, g_mid_mask, g_small_mask);
    fflush(stderr);
    (void)tidstr;
}

int main(int argc, char **argv) {
    if (argc < 5) {
        fprintf(stderr, "usage: %s <big_mask> <small_mask> <mid_mask> <pkg...>\n", argv[0]);
        return 1;
    }
    g_mask = strtoul(argv[1], NULL, 16);
    g_small_mask = strtoul(argv[2], NULL, 16);
    g_mid_mask = strtoul(argv[3], NULL, 16);
    for (int i = 4; i < argc && g_npkgs < 64; i++) {
        strncpy(g_pkgs[g_npkgs], argv[i], sizeof(g_pkgs[0]) - 1);
        g_npkgs++;
    }
    if (g_npkgs == 0) { fprintf(stderr, "no pkg\n"); return 1; }
    // 读核数
    FILE *f = fopen("/sys/devices/system/cpu/present", "r");
    if (f) {
        char buf[64] = {0};
        if (fgets(buf, sizeof(buf), f)) {
            int a = -1, b = -1;
            if (sscanf(buf, "%d-%d", &a, &b) == 2 && a >= 0 && b >= a) {
                for (int c = a; c <= b && c < 32; c++) g_all_mask |= (1UL << c);
            }
        }
        fclose(f);
    }
    if (g_all_mask == 0) g_all_mask = 0xFF;
    signal(SIGTERM, on_sig);
    signal(SIGINT, on_sig);
    fprintf(stderr, "fpsbind poll start big=0x%lx mid=0x%lx small=0x%lx all=0x%lx\n", g_mask, g_mid_mask, g_small_mask, g_all_mask);
    // 轮询：每 100ms 遍历目标进程线程绑核
    while (!g_stop) {
        for (int i = 0; i < g_npkgs; i++) {
            pid_t pid = find_pid(g_pkgs[i]);
            if (pid > 0) bind_pid(pid);
        }
        usleep(100000);
    }
    fprintf(stderr, "fpsbind exit\n");
    return 0;
}
