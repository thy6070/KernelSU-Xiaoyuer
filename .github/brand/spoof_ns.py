import os, sys
OLD="me.weishu.kernelsu"; NEW="com.tencent.THY"
OLD_PATH=OLD.replace(".","/"); NEW_PATH=NEW.replace(".","/")
OLD_US=OLD.replace(".","_"); NEW_US=NEW.replace(".","_")
ROOT="manager"
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
        if OLD not in s and OLD_PATH not in s and OLD_US not in s: continue
        s=s.replace(OLD,NEW).replace(OLD_PATH,NEW_PATH).replace(OLD_US,NEW_US)
        open(p,"w",encoding="utf-8",errors="surrogateescape").write(s)
        changed+=1
print("rewritten:",changed)
left=[]
for dirpath,_,files in os.walk(ROOT):
    for f in files:
        if os.path.splitext(f)[1] in EXTS:
            p=os.path.join(dirpath,f)
            try:
                if OLD in open(p,encoding="utf-8",errors="surrogateescape").read(): left.append(p)
            except Exception: pass
if left:
    print("LEFTOVER:",left[:3]); sys.exit(1)
print("clean")
