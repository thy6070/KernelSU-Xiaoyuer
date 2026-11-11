#!/system/bin/sh
# ============================================================
#  帧率方案适配检测（用于 KernelSU Manager 帧率悬浮窗）
#  用法：su -c "sh /sdcard/fps_check.sh"
#  输出：设备信息 / root 环境 / SurfaceFlinger 接口 / 图层格式 / 实测 FPS
# ============================================================

if [ "$(id -u)" != "0" ]; then
  echo "需要 root 权限，请用：su -c \"sh $0\""
  exit 1
fi

MAXLEN=18

echo "============================================================"
echo " 1. 设备信息"
echo "============================================================"
echo "型号      : $(getprop ro.product.model)"
echo "品牌      : $(getprop ro.product.brand)"
echo "Android   : $(getprop ro.build.version.release)  (API $(getprop ro.build.version.sdk))"
echo "芯片平台  : $(getprop ro.board.platform)"
echo "ROM       : $(getprop ro.build.version.incremental)"
echo "A/B       : $(getprop ro.build.ab_update)"

echo
echo "============================================================"
echo " 2. root 环境"
echo "============================================================"
if [ -x /data/adb/ksu/bin/su ]; then
  echo "root 方式 : KernelSU"
elif [ -x /data/adb/magisk/su ]; then
  echo "root 方式 : Magisk"
elif [ -x /system/bin/su ] || [ -x /system/xbin/su ]; then
  echo "root 方式 : 内置 su"
else
  echo "root 方式 : 未检出标准路径"
fi
echo "su 路径   : $(command -v su)"
echo "su 身份   : $(id -u):$(id -g)  context=$(cat /proc/self/attr/current 2>/dev/null)"

echo
echo "============================================================"
echo " 3. SurfaceFlinger 服务"
echo "============================================================"
service list 2>/dev/null | grep -E 'SurfaceFlinger' | head -5
if ! service list 2>/dev/null | grep -q 'SurfaceFlingerAIDL'; then
  echo "（未发现 SurfaceFlingerAIDL，可能是 Android 12 以下）"
fi

echo
echo "============================================================"
echo " 4. --list 输出格式（前 3 行）"
echo "============================================================"
dumpsys SurfaceFlinger --list 2>/dev/null | head -3
LISTFMT=$(dumpsys SurfaceFlinger --list 2>/dev/null | head -1)
case "$LISTFMT" in
  RequestedLayerState*) echo "格式判定 : 新版 RequestedLayerState{}（Android 14+）" ;;
  *) echo "格式判定 : 旧版纯图层名" ;;
esac

echo
echo "============================================================"
echo " 5. 前台应用与图层"
echo "============================================================"
FG=$(dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity)
echo "前台      : $FG"
PKG=$(echo "$FG" | sed -n 's/.*[ {]\([a-zA-Z0-9_]*\.[a-zA-Z0-9_.]*\)\/.*/\1/p')
echo "包名      : $PKG"
if [ -n "$PKG" ]; then
  echo "候选图层  :"
  dumpsys SurfaceFlinger --list 2>/dev/null | grep -F "$PKG" | head -8 | while IFS= read -r LINE; do
    L=$(echo "$LINE" | sed -E 's/^RequestedLayerState\{//; s/ parentId=.*$//')
    echo "   [$L]"
  done
fi

echo
echo "============================================================"
echo " 6. --latency 实测（找第一个有帧数据的层）"
echo "============================================================"
FOUND=0
if [ -n "$PKG" ]; then
  dumpsys SurfaceFlinger --list 2>/dev/null | grep -F "$PKG" | while IFS= read -r LINE; do
    L=$(echo "$LINE" | sed -E 's/^RequestedLayerState\{//; s/ parentId=.*$//')
    [ -z "$L" ] && continue
    OUT=$(dumpsys SurfaceFlinger --latency "$L" 2>/dev/null)
    N=$(echo "$OUT" | tail -n +2 | awk -F'\t' -v ml="$MAXLEN" 'NF>=2 && $2 ~ /^[0-9]+$/ && $2+0>0 && length($2)<=ml {c++} END{print c+0}')
    if [ "${N:-0}" -ge 3 ]; then
      FPS=$(echo "$OUT" | tail -n +2 | awk -F'\t' -v ml="$MAXLEN" '
        NF>=2 && $2 ~ /^[0-9]+$/ && $2+0>0 && length($2)<=ml { t[c]=$2+0; c++ }
        END {
          if (c < 3) { printf "0"; exit }
          for (i=0;i<c;i++) for (j=i+1;j<c;j++) if (t[j]<t[i]) { x=t[i]; t[i]=t[j]; t[j]=x }
          end=t[c-1]; win=end-600000000
          m=0; first=0
          for (i=0;i<c;i++) if (t[i]>=win) { if (m==0) first=t[i]; m++ }
          if (m < 2) { printf "0"; exit }
          printf "%.1f", (m-1)*1000000000/(end-first)
        }')
      echo "  ✔ 有帧图层 : $L"
      echo "    帧数     : $N"
      echo "    实测 FPS : $FPS"
      echo "RESULT_OK"
    fi
  done | tee /data/local/tmp/_fps_probe.txt
  FOUND=$(grep -c 'RESULT_OK' /data/local/tmp/_fps_probe.txt 2>/dev/null)
  rm -f /data/local/tmp/_fps_probe.txt
fi

echo
echo "============================================================"
echo " 7. 兜底数据源"
echo "============================================================"
if [ -r /sys/kernel/fpsgo/fstb/fpsgo_status ]; then
  echo "fpsgo_status : 存在"
  head -3 /sys/kernel/fpsgo/fstb/fpsgo_status
else
  echo "fpsgo_status : 不存在（非 MTK 或未启用）"
fi
DRMFPS=$(ls /sys/class/drm/*/measured_fps /sys/class/drm/card*/card*-*-*/measured_fps 2>/dev/null | head -1)
if [ -n "$DRMFPS" ]; then
  echo "drm measured_fps : $DRMFPS"
  cat "$DRMFPS" 2>/dev/null | head -2
else
  echo "drm measured_fps : 不存在"
fi

echo
echo "============================================================"
echo " 8. 结论"
echo "============================================================"
if [ "${FOUND:-0}" -ge 1 ]; then
  echo "适配等级 : A（完全适配，SurfaceFlinger 直读）"
elif dumpsys SurfaceFlinger --latency "$PKG" >/dev/null 2>&1; then
  echo "适配等级 : B（接口存在但当前无帧，需在游戏运行时复测）"
elif [ -r /sys/kernel/fpsgo/fstb/fpsgo_status ]; then
  echo "适配等级 : C（无 SF 数据，依赖 fpsgo 兜底）"
else
  echo "适配等级 : D（不建议适配，数据源缺失）"
fi
echo "报告结束。请把以上内容整段发回。"
