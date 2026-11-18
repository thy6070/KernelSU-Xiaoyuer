// fpsbinder2: AOSP libbinder C++ 客户端，直连 SurfaceFlinger 的 dump（帧时间戳）
// 链接符号运行时由设备 libbinder.so 解析（CI 里用 --unresolved-symbols=ignore-all）。
// 用法：fpsbinder2 <layer> [interval_ms] [output_file]
//
// 数据源自适应（同一进程内完成）：
//   1) 首选 SurfaceFlinger --latency <layer>：逐帧 present 时间戳（121 行），最准
//   2) 若该层 --latency 全为 0（部分 ROM 不记录 BLAST 层时间戳），
//      切到 --timestats 的【分层】统计，取该游戏层的 averageFPS（仍是游戏层，不是全局屏幕帧率）
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
#include <time.h>

using namespace android;

// --latency 输出里是否真有帧时间戳（第 2 行起出现非 "0\t0\t0"）
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

// 从层名里提取包名：..."com.tencent.tmgp.pubgmhd/..." → com.tencent.tmgp.pubgmhd
static std::string pkgOf(const std::string &layer) {
    size_t p = layer.find("com.");
    if (p == std::string::npos) return "";
    size_t e = layer.find('/', p);
    if (e == std::string::npos) e = layer.find(']', p);
    if (e == std::string::npos) e = layer.size();
    return layer.substr(p, e - p);
}

// 从 --timestats -dump 里取包含该包名的层的 averageFPS（游戏层，不是全局）
static double timestatsLayerFps(const std::string &d, const std::string &pkg) {
    if (pkg.empty()) return -1.0;
    size_t pos = 0;
    while (pos < d.size()) {
        size_t ln = d.find("layerName = ", pos);
        if (ln == std::string::npos) return -1.0;
        size_t le = d.find('\n', ln);
        size_t leSafe = (le == std::string::npos) ? d.size() : le;
        std::string lname = d.substr(ln + 12, leSafe - (ln + 12));
        size_t nextLayer = d.find("layerName = ", leSafe + 1);
        size_t segEnd = (nextLayer == std::string::npos) ? d.size() : nextLayer;
        if (lname.find(pkg) != std::string::npos) {
            size_t fp = d.find("averageFPS = ", leSafe);
            if (fp != std::string::npos && fp < segEnd) {
                return atof(d.c_str() + fp + 13);
            }
        }
        pos = leSafe + 1;
    }
    return -1.0;
}

// 从 --timestats -dump 里取包含该包名的层的 totalFrames（累计帧数）
static long long timestatsLayerFrames(const std::string &d, const std::string &pkg, bool *found) {
    *found = false;
    if (pkg.empty()) return 0;
    long long best = -1;
    size_t pos = 0;
    while (pos < d.size()) {
        size_t ln = d.find("layerName = ", pos);
        if (ln == std::string::npos) return 0;
        size_t le = d.find('\n', ln);
        size_t leSafe = (le == std::string::npos) ? d.size() : le;
        std::string lname = d.substr(ln + 12, leSafe - (ln + 12));
        size_t nextLayer = d.find("layerName = ", leSafe + 1);
        size_t segEnd = (nextLayer == std::string::npos) ? d.size() : nextLayer;
        if (lname.find(pkg) != std::string::npos && lname.find("SurfaceView") != std::string::npos) {
            size_t tf = d.find("totalFrames = ", leSafe);
            if (tf != std::string::npos && tf < segEnd) {
                long long v = atoll(d.c_str() + tf + 14);
                // 同一包可能有多个 SurfaceView（视频/光标/小窗），取累计帧数最大的主渲染层
                if (v > best) best = v;
            }
        }
        pos = leSafe + 1;
    }
    if (best >= 0) { *found = true; return best; }
    return 0;
}

static long long nowNs() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (long long) ts.tv_sec * 1000000000LL + ts.tv_nsec;
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
    std::string layer = argv[1];
    std::string pkg = pkgOf(layer);

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
    fprintf(stderr, "sf ok pkg=%s\n", pkg.c_str());

    bool tsMode = false;
    long long lastFrames = -1;
    long long lastNs = 0;
    double emaFps = -1.0;

    while (true) {
        if (!tsMode) {
            std::string data;
            Vector<String16> args;
            args.add(String16("--latency"));
            args.add(String16(argv[1]));
            if (!dumpTo(sf, args, data)) {
                usleep(intervalMs * 1000);
                continue;
            }
            if (!latencyHasFrames(data)) {
                // 只有游戏/视频的 SurfaceView 层才允许切 timestats；
                // 桌面、状态栏、系统动画层读不到帧就是没有帧率，不能显示
                if (layer.find("SurfaceView") == std::string::npos) {
                    usleep(intervalMs * 1000);
                    continue;
                }
                // 该层 --latency 全 0：切 timestats 按层统计（同一个游戏层）
                Vector<String16> en;
                en.add(String16("--timestats"));
                en.add(String16("-enable"));
                std::string tmp;
                dumpTo(sf, en, tmp);
                tsMode = true;
                fprintf(stderr, "latency all zero -> timestats per-layer mode (layer=%s pkg=%s)\n", argv[1], pkg.c_str());
                usleep(500 * 1000);
                continue;
            }
            writeOut(outFile, data);
            usleep(intervalMs * 1000);
        } else {
            Vector<String16> args;
            args.add(String16("--timestats"));
            args.add(String16("-dump"));
            std::string data;
            if (!dumpTo(sf, args, data)) {
                usleep(1000 * 1000);
                continue;
            }
            bool found = false;
            long long frames = timestatsLayerFrames(data, pkg, &found);
            static int missCount = 0;
            static bool firstFound = false;
            if (!found) {
                if (missCount++ % 10 == 0) fprintf(stderr, "timestats: layer not found for pkg=%s (try %d)\n", pkg.c_str(), missCount);
            } else if (!firstFound) {
                firstFound = true;
                fprintf(stderr, "timestats: matched pkg=%s totalFrames=%lld\n", pkg.c_str(), frames);
            }
            long long t = nowNs();
            if (found && lastFrames >= 0 && t > lastNs) {
                double dt = (t - lastNs) / 1e9;
                long long d = frames - lastFrames;
                // 异常保护：计数器回绕/重置、或时间太短时跳过
                if (d >= 0 && dt > 0.05) {
                    double v = d / dt;
                    if (v >= 0.0 && v < 2000.0) {
                        // EMA：新值权重更高，响应更快；首次直接采用
                        emaFps = (emaFps < 0.0) ? v : (emaFps * 0.30 + v * 0.70);
                        char b[32];
                        snprintf(b, sizeof b, "%.2f\n", emaFps);
                        writeOut(outFile, std::string(b));
                    }
                }
            }
            if (found) { lastFrames = frames; lastNs = t; }
            usleep(200 * 1000);
        }
    }
    return 0;
}
