// fpsdumpsys: dlopen Scene 的 binder-sdk35.so，进程内调用 Dumpsys::main
// 编译：clang++ -O2 -std=c++17 native/fpsdumpsys.cpp -o fpsdumpsys -lc++_shared -ldl
// 用法：fpsdumpsys <layer>
#include <dlfcn.h>
#include <cstdio>
#include <cstring>
#include <string>

// android::Dumpsys::main(int, char**, std::string&)
static const char *kSym =
    "_ZN7android7Dumpsys4mainEiPKPcRNSt3__112basic_stringIcNS4_11char_traitsIcEENS4_9allocatorIcEEEE";

typedef void (*dumpsys_fn)(int, char **, std::string &);

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer>\n", argv[0]);
        return 1;
    }
    const char *libs[] = {
        "/data/local/tmp/binder-sdk35.so",
        "/data/user/0/com.tencent.xiaoyuer/files/binder-sdk35.so",
    };
    void *h = nullptr;
    for (auto p : libs) {
        h = dlopen(p, RTLD_NOW | RTLD_GLOBAL);
        if (h) { fprintf(stderr, "loaded %s\n", p); break; }
    }
    if (!h) {
        fprintf(stderr, "dlopen failed: %s\n", dlerror());
        return 1;
    }
    auto fn = (dumpsys_fn)dlsym(h, kSym);
    if (!fn) {
        fprintf(stderr, "dlsym failed: %s\n", dlerror());
        return 1;
    }
    std::string out;
    char *args[] = {(char *)"dumpsys", (char *)"SurfaceFlinger", (char *)"--latency", argv[1]};
    fn(4, args, out);
    fputs(out.c_str(), stdout);
    return 0;
}
