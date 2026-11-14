#define _GNU_SOURCE
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

static unsigned long g_mask = 0;      // 大核
static unsigned long g_small_mask = 0; // 小核
static unsigned long g_mid_mask = 0;   // 中核
static unsigned char g_bound[65536];   // 只绑一次
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
        if (g_bound[tid]) continue;
        snprintf(tpath, sizeof(tpath), "/proc/%d/task/%d", (int)pid, tid);
        int fd = open(tpath, O_RDONLY);
        if (fd >= 0) close(fd);
        char tcomm[64] = {0};
        unsigned long use = g_mask;
        if (read_comm(tid, tcomm, sizeof(tcomm)) == 0) {
            int is_render = (strstr(tcomm, "Render") || strstr(tcomm, "render") ||
                             strstr(tcomm, "GL") || strstr(tcomm, "gfx") ||
                             strstr(tcomm, "Unity") || strstr(tcomm, "main"));
            int is_low = (strstr(tcomm, "Audio") || strstr(tcomm, "audio") ||
                          strstr(tcomm, "Net") || strstr(tcomm, "net") ||
                          strstr(tcomm, "binder") || strstr(tcomm, "io"));
            if (is_render) use = g_mask | g_mid_mask;
            else if (is_low && g_small_mask) use = g_small_mask;
            else if (g_mid_mask) use = g_mid_mask;
            fprintf(stderr, "  tid=%d comm=%s type=%s use=0x%lx\n", tid, tcomm,
                    is_render ? "BIG" : (is_low ? "SMALL" : "MID"), use);
        }
        CPU_ZERO(&set);
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
        fprintf(stderr, "usage: %s <big> <small> <mid> <pkg...>\n", argv[0]);
        return 1;
    }
    g_mask = strtoul(argv[1], NULL, 16);
    g_small_mask = strtoul(argv[2], NULL, 16);
    g_mid_mask = strtoul(argv[3], NULL, 16);
    for (int i = 4; i < argc && g_npkgs < 64; i++) {
        strncpy(g_pkgs[g_npkgs], argv[i], sizeof(g_pkgs[0]) - 1);
        g_npkgs++;
    }
    if (g_npkgs == 0) {
        fprintf(stderr, "no pkg given\n");
        return 1;
    }

    signal(SIGTERM, on_sig);
    signal(SIGINT, on_sig);

    int s = socket(AF_NETLINK, SOCK_DGRAM, NETLINK_CONNECTOR);
    if (s < 0) {
        fprintf(stderr, "netlink socket: %s\n", strerror(errno));
        return 1;
    }

    struct sockaddr_nl sa;
    memset(&sa, 0, sizeof(sa));
    sa.nl_family = AF_NETLINK;
    sa.nl_groups = CN_IDX_PROC;
    sa.nl_pid = getpid();
    if (bind(s, (struct sockaddr *)&sa, sizeof(sa)) < 0) {
        fprintf(stderr, "bind: %s\n", strerror(errno));
        close(s);
        return 1;
    }

    // 订阅进程事件
    char buf[4096];
    memset(buf, 0, sizeof(buf));
    struct nlmsghdr *nlh = (struct nlmsghdr *)buf;
    struct cn_msg *cn = (struct cn_msg *)NLMSG_DATA(nlh);
    struct proc_cn_mcast_op *mc = (struct proc_cn_mcast_op *)cn->data;
    nlh->nlmsg_len = NLMSG_LENGTH(sizeof(*cn) + sizeof(*mc));
    nlh->nlmsg_type = NLMSG_DONE;
    nlh->nlmsg_pid = getpid();
    nlh->nlmsg_flags = 0;
    cn->id.idx = CN_IDX_PROC;
    cn->id.val = CN_VAL_PROC;
    cn->len = sizeof(*mc);
    *mc = PROC_CN_MCAST_LISTEN;
    if (send(s, nlh, nlh->nlmsg_len, 0) < 0) {
        fprintf(stderr, "subscribe: %s\n", strerror(errno));
        close(s);
        return 1;
    }

    fprintf(stderr, "fpsbind: mask=0x%lx pkgs=%d\n", g_mask, g_npkgs);

    // 先对已存在的目标进程绑一次
    DIR *pd = opendir("/proc");
    if (pd) {
        struct dirent *de;
        char comm[128];
        while ((de = readdir(pd))) {
            int pid = atoi(de->d_name);
            if (pid <= 0) continue;
            if (read_comm(pid, comm, sizeof(comm)) == 0 && pkg_match(comm)) bind_pid(pid);
        }
        closedir(pd);
    }

    while (g_run) {
        ssize_t n = recv(s, buf, sizeof(buf), 0);
        if (n <= 0) {
            if (errno == EINTR) continue;
            break;
        }
        for (nlh = (struct nlmsghdr *)buf; NLMSG_OK(nlh, (unsigned)n); nlh = NLMSG_NEXT(nlh, n)) {
            if (nlh->nlmsg_type == NLMSG_NOOP) continue;
            cn = (struct cn_msg *)NLMSG_DATA(nlh);
            if (cn->id.idx != CN_IDX_PROC || cn->id.val != CN_VAL_PROC) continue;
            struct proc_event *ev = (struct proc_event *)cn->data;
            if (ev->what == PROC_EVENT_EXEC || ev->what == PROC_EVENT_FORK) {
                pid_t pid = ev->what == PROC_EVENT_EXEC
                            ? ev->event_data.exec.process_pid
                            : ev->event_data.fork.child_pid;
                char comm[128];
                if (read_comm(pid, comm, sizeof(comm)) == 0 && pkg_match(comm)) {
                    bind_pid(pid);
                }
            }
        }
    }

    // 取消订阅
    nlh = (struct nlmsghdr *)buf;
    cn = (struct cn_msg *)NLMSG_DATA(nlh);
    mc = (struct proc_cn_mcast_op *)cn->data;
    nlh->nlmsg_len = NLMSG_LENGTH(sizeof(*cn) + sizeof(*mc));
    nlh->nlmsg_type = NLMSG_DONE;
    nlh->nlmsg_pid = getpid();
    cn->id.idx = CN_IDX_PROC;
    cn->id.val = CN_VAL_PROC;
    cn->len = sizeof(*mc);
    *mc = PROC_CN_MCAST_IGNORE;
    send(s, nlh, nlh->nlmsg_len, 0);

    close(s);
    return 0;
}
