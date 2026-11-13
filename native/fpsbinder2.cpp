// fpsbinder2: AOSP libbinder C++ 客户端，直连 SurfaceFlinger 的 dump（帧时间戳）
// 链接符号运行时由设备 libbinder.so 解析（CI 里用 --unresolved-symbols=ignore-all）。
// 用法：fpsbinder2 <layer>
#include <binder/ProcessState.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/Parcel.h>
#include <utils/String16.h>
#include <utils/String8.h>

#include <cstdio>
#include <cstring>
#include <unistd.h>
#include <dlfcn.h>

using namespace android;

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer>\n", argv[0]);
        return 1;
    }
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

    int fds[2];
    if (pipe(fds) < 0) { perror("pipe"); return 1; }

    Parcel data;
    data.writeInterfaceToken(String16("android.gui.ISurfaceComposer"));
    data.writeInt32(1);
    data.writeFileDescriptor(fds[1]);
    data.writeInt32(2);
    data.writeString16(String16("--latency"));
    data.writeString16(String16(argv[1]));

    Parcel reply;
    status_t st = sf->transact(1, data, &reply);
    fprintf(stderr, "transact=%d\n", (int)st);
    if (st != 0) return 1;
    close(fds[1]);

    char buf[8192];
    ssize_t n;
    while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
        fwrite(buf, 1, (size_t)n, stdout);
    }
    close(fds[0]);
    return 0;
}
