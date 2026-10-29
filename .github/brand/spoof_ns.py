import os, sys

OLD="me.weishu.kernelsu"; NEW="com.tencent.tmgp.kingdom"
CLASS_REPLACEMENTS = [
    ("KernelSUApplication", "XYApplication"),
    ("FlashAnyKernel", "XYKernel"),
    ("FlashModule", "XYModule"),
    ("magica", "mgr"),
    ("MagicaService", "MgrService"),
    ("AppZygotePreload", "AppZPreload"),
    ("BootCompletedReceiver", "BootRecv"),
]
FILE_RENAMES = [
    ("BootCompletedReceiver.java", "BootRecv.java"),
    ("MagicaService.java", "MgrService.java"),
    ("AppZygotePreload.java", "AppZPreload.java"),
    ("KernelSUApplication.java", "XYApplication.java"),
    ("KernelSUApplication.kt", "XYApplication.kt"),
]
OLD_PATH=OLD.replace(".","/"); NEW_PATH=NEW.replace(".","/")
OLD_US=OLD.replace(".","_"); NEW_US=NEW.replace(".","_")
ROOT="manager"

for dirpath, dirnames, _ in os.walk(ROOT):
    for d in list(dirnames):
        if d == "magica":
            src=os.path.join(dirpath,d); dst=os.path.join(dirpath,"mgr")
            if not os.path.exists(dst):
                os.rename(src,dst)

targets=[]
for dirpath, dirnames, _ in os.walk(ROOT):
    for d in list(dirnames):
        p=os.path.join(dirpath,d)
        if p.replace(os.sep,"/").endswith("/"+OLD_PATH): targets.append(p)
for p in sorted(targets,key=lambda x:x.count(os.sep),reverse=True):
    newp=p.replace("/"+OLD_PATH,"/"+NEW_PATH)
    os.makedirs(os.path.dirname(newp),exist_ok=True)
    os.rename(p,newp)

EXTS={".kt",".kts",".java",".xml",".aidl",".cc",".cpp",".c",".h",".hpp",".txt",".pro",".gradle",".properties",".json",".yml",".yaml",".cmake"}
changed=0
for dirpath,_,files in os.walk(ROOT):
    for f in files:
        if os.path.splitext(f)[1] not in EXTS: continue
        p=os.path.join(dirpath,f)
        try: s=open(p,encoding="utf-8",errors="surrogateescape").read()
        except Exception: continue
        orig=s
        s=s.replace(OLD,NEW).replace(OLD_PATH,NEW_PATH).replace(OLD_US,NEW_US)
        for a,b in CLASS_REPLACEMENTS: s=s.replace(a,b)
        if s!=orig:
            open(p,"w",encoding="utf-8",errors="surrogateescape").write(s)
            changed+=1
print("rewritten:",changed)

# rename files so public classes match
for dirpath,_,files in os.walk(ROOT):
    for f in files:
        for a,b in FILE_RENAMES:
            if f == a:
                src=os.path.join(dirpath,f)
                dst=os.path.join(dirpath,b)
                if not os.path.exists(dst):
                    os.rename(src,dst)
                    print("mv", a, "->", b)

print("clean")
