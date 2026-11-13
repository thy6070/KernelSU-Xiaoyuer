// fpsbinder2: AOSP libbinder C++ 客户端，直连 SurfaceFlinger 的 dump（帧时间戳）
// 链接符号运行时由设备 libbinder.so 解析（CI 里用 --unresolved-symbols=ignore-all）。
// 用法：fpsbinder2 <layer>
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
#include <dlfcn.h>

using namespace android;

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer> [interval_ms] [output_file]\n", argv[0]);
        return 1;
    }
    int intervalMs = argc > 2 ? atoi(argv[2]) : 80;
    const char *outFile = argc > 3 ? argv[3] : nullptr;
    // 运行时加载设备库（链接时符号未解析）
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

    Vector<String16> args;
    args.add(String16("--latency"));
    args.add(String16(argv[1]));

    while (true) {
        int fds[2];
        if (pipe(fds) < 0) { perror("pipe"); break; }

        status_t st = sf->dump(fds[1], args);
        close(fds[1]);
        if (st != 0) {
            fprintf(stderr, "dump=%d\n", (int)st);
            usleep(intervalMs * 1000);
            continue;
        }
        // 读数据
        std::string data;
        char buf[8192];
        ssize_t n;
        while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
            data.append(buf, (size_t)n);
        }
        close(fds[0]);
        if (!data.empty()) {
            if (outFile) {
                FILE *f = fopen(outFile, "w");
                if (f) {
                    fwrite(data.data(), 1, data.size(), f);
                    fclose(f);
                }
            } else {
                fputs(data.c_str(), stdout);
                fflush(stdout);
            }
        }
        usleep(intervalMs * 1000);
    }
    return 0;
}
