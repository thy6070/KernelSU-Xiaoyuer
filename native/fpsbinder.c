// fpsbinder: 直接通过 /dev/binder 调用 SurfaceFlinger 的 dump 接口，
// 进程内拿到 --latency 数据（零 fork），替代 shell 里的 dumpsys。
// 编译：aarch64-linux-android26-clang -O2 native/fpsbinder.c -o fpsbinder
// 用法：fpsbinder <layer>   输出与 dumpsys SurfaceFlinger --latency <layer> 相同
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <stdint.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/uio.h>

typedef int32_t binder_size_t;
typedef uintptr_t binder_uintptr_t;

struct binder_write_read {
    binder_size_t write_size;
    binder_size_t write_consumed;
    binder_uintptr_t write_buffer;
    binder_size_t read_size;
    binder_size_t read_consumed;
    binder_uintptr_t read_buffer;
};

struct binder_transaction_data {
    union { size_t handle; void *ptr; } target;
    union { size_t code; unsigned int flags; } code;
    unsigned int flags;
    pid_t sender_pid;
    uid_t sender_euid;
    binder_size_t data_size;
    binder_size_t offsets_size;
    union {
        struct { binder_uintptr_t buffer; binder_uintptr_t offsets; } ptr;
        uint8_t buf[8];
    } data;
};

struct flat_binder_object {
    uint32_t type;
    uint32_t flags;
    binder_uintptr_t binder;
    uint32_t cookie;
    uint32_t pad;
};

#define BINDER_TYPE_FD 0x73663d42

#define BINDER_WRITE_READ _IOWR('b', 1, struct binder_write_read)
#define BC_TRANSACTION _IOW('c', 0, struct binder_transaction_data)
#define BR_TRANSACTION 0x40407202
#define BR_REPLY 0x40407203

/* 简单 parcel 构造 */
struct parcel {
    uint8_t buf[4096];
    size_t len;
    size_t offsets[16];
    size_t nocts;
};

static void p_init(struct parcel *p) { memset(p, 0, sizeof(*p)); }
static void p_i32(struct parcel *p, int32_t v) { memcpy(p->buf + p->len, &v, 4); p->len += 4; }
static void p_i64(struct parcel *p, int64_t v) { memcpy(p->buf + p->len, &v, 8); p->len += 8; }
static void p_bytes(struct parcel *p, const void *d, size_t n) { memcpy(p->buf + p->len, d, n); p->len += n; }

/* String16：len(含结束符) + UTF-16 + 4 对齐 */
static void p_string16(struct parcel *p, const char *s) {
    size_t n = strlen(s);
    int32_t len = (int32_t)n + 1;
    p_i32(p, len);
    for (size_t i = 0; i < n; i++) {
        uint16_t c = (uint16_t)s[i];
        memcpy(p->buf + p->len, &c, 2);
        p->len += 2;
    }
    uint16_t z = 0;
    memcpy(p->buf + p->len, &z, 2);
    p->len += 2;
    while (p->len % 4) p->buf[p->len++] = 0;
}

static int binder_ready = 0;
static int binder_fd = -1;
static void *binder_map = NULL;
static uint8_t rbuf[8192];

static int binder_open_dev(void) {
    const char *paths[] = {"/dev/binderfs/binder", "/dev/binder"};
    for (int i = 0; i < 2; i++) {
        int fd = open(paths[i], O_RDWR | O_CLOEXEC);
        if (fd < 0) continue;
        void *m = mmap(NULL, 1 << 20, PROT_READ, MAP_PRIVATE, fd, 0);
        if (m == MAP_FAILED) { close(fd); continue; }
        binder_fd = fd;
        binder_map = m;
        return 0;
    }
    return -1;
}

/* 发一个事务并读回复；returns 回复 data 长度，-1 失败 */
static int binder_call(const struct parcel *p, size_t code, size_t handle,
                       size_t *offsets, size_t nocts, struct parcel *rep) {
    (void)offsets;
    static uint8_t wbuf[8192];
    struct binder_transaction_data *tr = (struct binder_transaction_data *)(wbuf + 8);
    memset(wbuf, 0, sizeof(wbuf));
    uint32_t cmd = BC_TRANSACTION;
    memcpy(wbuf, &cmd, 4);
    size_t need = p->len;
    memcpy(tr->data.buf, &need, 8); /* 先用 8 字节占位，下面覆盖 */
    /* 事务体 */
    memset(tr, 0, sizeof(*tr));
    tr->target.handle = handle;
    tr->code.code = code;
    tr->data_size = p->len;
    tr->offsets_size = nocts * sizeof(binder_size_t);
    /* 数据缓冲区紧跟在 tr 之后 */
    uint8_t *payload = (uint8_t *)tr + sizeof(*tr);
    memcpy(payload, p->buf, p->len);
    binder_uintptr_t data_ptr = (binder_uintptr_t)payload;
    binder_uintptr_t off_ptr = (binder_uintptr_t)(payload + p->len);
    memcpy((void *)off_ptr, offsets ? offsets : (size_t *)"", nocts * sizeof(binder_size_t));
    tr->data.ptr.buffer = data_ptr;
    tr->data.ptr.offsets = off_ptr;
    size_t total = 4 + sizeof(*tr) + p->len + tr->offsets_size;

    struct binder_write_read bwr;
    memset(&bwr, 0, sizeof(bwr));
    bwr.write_size = total;
    bwr.write_buffer = (binder_uintptr_t)wbuf;
    bwr.read_size = sizeof(rbuf);
    bwr.read_buffer = (binder_uintptr_t)rbuf;

    /* 循环：先写，读回复 */
    for (int round = 0; round < 8; round++) {
        if (ioctl(binder_fd, BINDER_WRITE_READ, &bwr) < 0) return -1;
        /* 解析读缓冲 */
        size_t off = 0;
        while (off < bwr.read_consumed) {
            uint32_t *c = (uint32_t *)(rbuf + off);
            uint32_t cc = *c;
            if (cc == BR_REPLY) {
                struct binder_transaction_data *r = (struct binder_transaction_data *)(rbuf + off + 4);
                if (rep) {
                    memset(rep, 0, sizeof(*rep));
                    size_t n = r->data_size;
                    if (n > sizeof(rep->buf)) n = sizeof(rep->buf);
                    memcpy(rep->buf, (void *)r->data.ptr.buffer, n);
                    rep->len = n;
                }
                return 0;
            }
            off += 4 + sizeof(struct binder_transaction_data);
        }
        /* 没有回复：继续读 */
        bwr.write_size = 0;
        bwr.read_consumed = 0;
    }
    return -1;
}

/* servicemanager 查服务：handle 0, code 1 = GET_SERVICE_TRANSACTION */
static int get_service(const char *name, uint32_t *out_handle) {
    struct parcel p;
    p_init(&p);
    /* AOSP: writeInterfaceToken + writeString16(name) */
    p_string16(&p, "android.os.IServiceManager");
    p_string16(&p, name);
    struct parcel rep;
    if (binder_call(&p, 1, 0, NULL, 0, &rep) < 0) return -1;
    if (rep.len < 4) return -1;
    /* 回复可能是 flat_binder_object(BINDER_TYPE_HANDLER) 或直接 int32 handle */
    uint32_t type = 0;
    memcpy(&type, rep.buf, 4);
    if (type == 0x73685d42u || type == 0x73622a85u) {
        /* type 4 + flags 4 = 8，binder 字段从 8 开始；取低 32 位作 handle */
        if (rep.len < 12) return -1;
        memcpy(out_handle, rep.buf + 8, 4);
        return 0;
    }
    /* 直接 int32 */
    memcpy(out_handle, rep.buf, 4);
    return 0;
}

int main(int argc, char **argv) {
    if (argc < 2) {
        fprintf(stderr, "usage: %s <layer>\n", argv[0]);
        return 1;
    }
    if (binder_open_dev() < 0) {
        fprintf(stderr, "open binder failed: %s\n", strerror(errno));
        return 1;
    }
    uint32_t sf = 0;
    if (get_service("SurfaceFlinger", &sf) < 0) {
        fprintf(stderr, "get_service SurfaceFlinger failed\n");
        return 1;
    }
    fprintf(stderr, "SurfaceFlinger handle=%u\n", sf);

    /* 调 dump：code 1，parcel = fd 数组 + 参数数组 */
    int fds[2];
    if (pipe(fds) < 0) { fprintf(stderr, "pipe: %s\n", strerror(errno)); return 1; }
    int wfd = fds[1];

    struct parcel p;
    p_init(&p);
    p_i32(&p, 1);            /* num fds */
    /* 写 fd：用 flat_binder_object(BINDER_TYPE_FD) */
    size_t fd_off = p.len;
    struct flat_binder_object fo;
    memset(&fo, 0, sizeof(fo));
    fo.type = BINDER_TYPE_FD;
    fo.binder = (binder_uintptr_t)wfd;
    p_bytes(&p, &fo, sizeof(fo));
    /* 参数：--latency <layer> */
    p_i32(&p, 3);
    p_string16(&p, "--latency");
    p_string16(&p, argv[1]);

    size_t sent_offsets[1] = { fd_off };
    struct parcel rep;
    if (binder_call(&p, 1, sf, sent_offsets, 1, &rep) < 0) {
        fprintf(stderr, "dump call failed: %s\n", strerror(errno));
        return 1;
    }
    close(wfd);

    /* 读管道里的 latency 文本 */
    char buf[8192];
    ssize_t n;
    while ((n = read(fds[0], buf, sizeof(buf))) > 0) {
        fwrite(buf, 1, (size_t)n, stdout);
    }
    close(fds[0]);
    return 0;
}
