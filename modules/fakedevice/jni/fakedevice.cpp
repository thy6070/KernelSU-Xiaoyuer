// 机型伪装 Zygisk 模块 v0.2
// 依赖 Zygisk Next（zygisksu）注入
#include <string>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <unistd.h>
#include <sys/types.h>
#include <sys/sysmacros.h>
#include <sys/system_properties.h>
#include "zygisk.hpp"

using zygisk::Api;
using zygisk::AppSpecializeArgs;

#define CONFIG "/data/adb/modules/fakedevice/config.conf"

static std::string g_pkg;
static char g_key[8][PROP_NAME_MAX];
static char g_val[8][PROP_VALUE_MAX];
static int g_n = 0;
static dev_t libc_dev = 0;
static ino_t libc_ino = 0;
static int (*orig_get)(const char *, char *) = nullptr;

static bool find_libc() {
    FILE *f = fopen("/proc/self/maps", "r");
    if (!f) return false;
    char line[512];
    while (fgets(line, sizeof(line), f)) {
        if (!strstr(line, "libc.so")) continue;
        unsigned int maj = 0, min = 0;
        unsigned long ino = 0;
        if (sscanf(line, "%*s %*s %*s %x:%x %lu", &maj, &min, &ino) == 3) {
            libc_dev = makedev(maj, min);
            libc_ino = ino;
            fclose(f);
            return true;
        }
    }
    fclose(f);
    return false;
}

static void load_config(const char *pkg) {
    g_n = 0;
    FILE *f = fopen(CONFIG, "r");
    if (!f) return;
    char line[512];
    while (fgets(line, sizeof(line), f) && g_n < 8) {
        char *nl = strchr(line, '\n');
        if (nl) *nl = 0;
        char *bar = strchr(line, '|');
        if (!bar) continue;
        *bar = 0;
        if (strcmp(line, pkg) != 0) continue;
        char *eq = strchr(bar + 1, '=');
        if (!eq) continue;
        *eq = 0;
        snprintf(g_key[g_n], PROP_NAME_MAX, "%s", bar + 1);
        snprintf(g_val[g_n], PROP_VALUE_MAX, "%s", eq + 1);
        g_n++;
    }
    fclose(f);
}

static int my_get(const char *name, char *value) {
    if (g_n > 0 && name && value) {
        for (int i = 0; i < g_n; i++) {
            if (strcmp(name, g_key[i]) == 0) {
                snprintf(value, PROP_VALUE_MAX, "%s", g_val[i]);
                return (int) strlen(g_val[i]);
            }
        }
    }
    return orig_get ? orig_get(name, value) : __system_property_get(name, value);
}

class FakeDevice : public zygisk::ModuleBase {
public:
    void onLoad(Api *api, JNIEnv *env) override {
        this->api = api;
        this->env = env;
    }

    void preAppSpecialize(AppSpecializeArgs *args) override {
        const char *n = args->nice_name ? env->GetStringUTFChars(args->nice_name, nullptr) : nullptr;
        if (!n) return;
        g_pkg = n;
        env->ReleaseStringUTFChars(args->nice_name, n);
        load_config(g_pkg.c_str());
        if (g_n <= 0) return;
        if (!find_libc()) return;
        api->pltHookRegister(libc_dev, libc_ino, "__system_property_get", (void *) my_get, (void **) &orig_get);
        api->pltHookCommit();
    }

private:
    Api *api = nullptr;
    JNIEnv *env = nullptr;
};

REGISTER_ZYGISK_MODULE(FakeDevice)
