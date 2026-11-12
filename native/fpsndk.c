// fpsndk: 用 NDK 的 libbinder_ndk（C API）直连 SurfaceFlinger dump，零 fork。
// 编译（CI/NDK）：aarch64-linux-android26-clang -O2 native/fpsndk.c -o fpsndk -lbinder_ndk -llog
// 用法：fpsndk <layer>
#include <android/binder_ibinder.h>
#include <android/binder_parcel.h>
#include <android/binder_status.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

// NDK 无 binder_manager.h：手动声明
__attribute__((weak)) AIBinder* AServiceManager_getService(const char* instance);

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer>\n", argv[0]);
        return 1;
    }
    AIBinder *sf = AServiceManager_getService("SurfaceFlinger");
    if (!sf) {
        fprintf(stderr, "getService SurfaceFlinger failed\n");
        return 1;
    }
    fprintf(stderr, "binder=%p\n", (void *)sf);

    AParcel *in = NULL;
    binder_status_t st = AIBinder_prepareTransaction(sf, &in);
    fprintf(stderr, "prepare=%d in=%p\n", (int)st, (void *)in);
    if (st != STATUS_OK || !in) {
        in = AParcel_create();
        fprintf(stderr, "fallback create in=%p\n", (void *)in);
    }
    if (!in) {
        fprintf(stderr, "no parcel\n");
        return 1;
    }
    fprintf(stderr, "parcel size before write=%d\n", (int)AParcel_getDataSize(in));

    // AOSP BpSurfaceComposer::dump:
    //   writeInterfaceToken; writeInt32(1); writeFileDescriptor(fd);
    //   writeInt32(args.size()); for arg: writeString16
    st = AParcel_writeString(in, "android.gui.ISurfaceComposer", (int32_t)strlen("android.gui.ISurfaceComposer"));
    fprintf(stderr, "token write=%d size=%d\n", (int)st, (int)AParcel_getDataSize(in));

    int fds[2];
    if (pipe(fds) < 0) { perror("pipe"); return 1; }
    AParcel_writeInt32(in, 1);
    st = AParcel_writeParcelFileDescriptor(in, fds[1]);
    fprintf(stderr, "fd write=%d\n", (int)st);
    AParcel_writeInt32(in, 2);
    AParcel_writeString(in, "--latency", (int32_t)strlen("--latency"));
    AParcel_writeString(in, argv[1], (int32_t)strlen(argv[1]));

    // 发送前把 data position 归零
    AParcel_setDataPosition(in, 0);
    AParcel *out = NULL;
    st = AIBinder_transact(sf, 1, &in, &out, 0);
    fprintf(stderr, "transact=%d out=%p\n", (int)st, (void *)out);
    if (st != STATUS_OK) {
        return 1;
    }
    close(fds[1]);

    char buf[8192];
    ssize_t n;
    while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
        fwrite(buf, 1, (size_t)n, stdout);
    }
    close(fds[0]);
    return 0;
}
