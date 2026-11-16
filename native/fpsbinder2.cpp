// fpsbinder2: AOSP libbinder C++ 客户端，直连 SurfaceFlinger 的 dump（帧时间戳）
// 链接符号运行时由设备 libbinder.so 解析（CI 里用 --unresolved-symbols=ignore-all）。
// 用法：fpsbinder2 <layer> [interval_ms] [output_file]
//
// 自适应两种数据源：
//   1) 默认走 SurfaceFlinger --latency <layer>（121 行：刷新周期 + 128 帧槽）
//   2) 若该层 --latency 全为 0（部分 ROM/设备 BLAST 层不吐时间戳），
//      自动切到 SurfaceFlinger 的 FPS ring buffer（每秒一条全局 fps），写纯数字输出。
#include <binder/ProcessState.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <utils/String16.h>
#include <utils/Vector.h>
#include <utils/String8.h>

#include <cstdio>
#include <cstring>
#include <unistd.h>
#include <cstdlib>
#include <string>
#include <dlfcn.h>

using namespace android;

// --latency 输出里是否真有帧时间戳（第 2 行起，出现非 "0\t0\t0" 的行）
static bool latencyHasFrames(const std::string &d) {
    size_t i = d.find('\n');
    if (i == std::string::npos) return false;
    i++;
    int checked = 0;
    while (i < d.size() && checked < 4) {
        size_t e = d.find('\n', i);
        if (e == std::string::npos) e = d.size();
        std::string line = d.substr(i, e - i);
        if (!line.empty()) {
            if (line.find("0\t0\t0") == std::string::npos) return true;
            checked++;
        }
        i = e + 1;
    }
    return false;
}

// 从完整 SurfaceFlinger dump 里取 "FPS ring buffer:" 之后时间最新的一条 fps
static double ringFps(const std::string &d) {
    size_t p = d.find("FPS ring buffer:");
    if (p == std::string::npos) return -1.0;
    size_t lim = p + 3000;
    if (lim > d.size()) lim = d.size();
    double best = -1.0;
    std::string bestT;
    size_t i = p;
    while (i < lim) {
        size_t e = d.find('\n', i);
        if (e == std::string::npos) e = lim;
        std::string line = d.substr(i, e - i);
        size_t fp = line.find("fps=");
        if (fp != std::string::npos) {
            size_t close = line.find(')');
            std::string t = (close != std::string::npos) ? line.substr(close + 1, 16) : "";
            double v = atof(line.c_str() + fp + 4);
            if (v > 0.0 && (best < 0.0 || t > bestT)) {
                best = v;
                bestT = t;
            }
        }
        i = e + 1;
    }
    return best;
}

static bool dumpTo(android::sp<IBinder> &sf, const Vector<String16> &args, std::string &out) {
    int fds[2];
    if (pipe(fds) < 0) return false;
    status_t st = sf->dump(fds[1], args);
    close(fds[1]);
    if (st != 0) {
        close(fds[0]);
        return false;
    }
    char buf[16384];
    ssize_t n;
    while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
        out.append(buf, (size_t) n);
    }
    close(fds[0]);
    return true;
}

static void writeOut(const char *outFile, const std::string &data) {
    if (outFile) {
        FILE *f = fopen(outFile, "w");
        if (f) {
            fwrite(data.data(), 1, data.size(), f);
            fclose(f);
        }
    } else {
        fwrite(data.data(), 1, data.size(), stdout);
        fflush(stdout);
    }
}

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer> [interval_ms] [output_file]\n", argv[0]);
        return 1;
    }
    int intervalMs = argc > 2 ? atoi(argv[2]) : 80;
    const char *outFile = argc > 3 ? argv[3] : nullptr;

    dlopen("libbinder.so", RTLD_NOW | RTLD_GLOBAL);
    dlopen("libutils.so", RTLD_NOW | RTLD_GLOBAL);

    sp<IServiceManager> sm = defaultServiceManager();
    if (sm == nullptr) {
        fprintf(stderr, "defaultServiceManager null\n");
        return 1;
    }
    sp<IBinder> sf = sm->getService(String16("SurfaceFlinger"));
    if (sf == nullptr) {
        fprintf(stderr, "SurfaceFlinger not found\n");
        return 1;
    }
    fprintf(stderr, "sf ok\n");

    bool ringMode = false;

    while (true) {
        std::string data;
        if (!ringMode) {
            Vector<String16> args;
            args.add(String16("--latency"));
            args.add(String16(argv[1]));
            if (!dumpTo(sf, args, data)) {
                usleep(intervalMs * 1000);
                continue;
            }
            if (!latencyHasFrames(data)) {
                // 该层 --latency 全 0：切 ring buffer 模式
                ringMode = true;
                fprintf(stderr, "latency all zero -> ring buffer mode\n");
                usleep(200 * 1000);
                continue;
            }
            writeOut(outFile, data);
            usleep(intervalMs * 1000);
        } else {
            Vector<String16> empty;
            if (!dumpTo(sf, empty, data)) {
                usleep(1000 * 1000);
                continue;
            }
            double v = ringFps(data);
            if (v > 0.0) {
                char b[32];
                snprintf(b, sizeof b, "%.2f\n", v);
                writeOut(outFile, std::string(b));
            }
            usleep(1000 * 1000);
        }
    }
    return 0;
}
