#!/usr/bin/env python3
"""在官方 KernelSU 源码上原地打补丁：让 oh_my_keymint 走 builtin 直达，不影响模块列表 WebUI。"""
import io
import sys

WH = 'src/manager/app/src/main/java/me/weishu/kernelsu/ui/webui/WebViewHelper.kt'
MANIFEST = 'src/manager/app/src/main/AndroidManifest.xml'

def patch_webview_helper():
    s = io.open(WH, encoding='utf-8').read()
    if 'oh_my_keymint' in s:
        print('WebViewHelper already patched')
        return
    old = 'val moduleInfo = modules.find { info -> info.id == moduleId }'
    if old not in s:
        print('ERROR: moduleInfo line not found', file=sys.stderr)
        sys.exit(1)
    new = (
        'val moduleInfo = modules.find { info -> info.id == moduleId } ?: run {\n'
        '            // builtin 直达：oh_my_keymint 不在 modules 列表里\n'
        '            if (moduleId == "oh_my_keymint") {\n'
        '                me.weishu.kernelsu.data.model.Module(\n'
        '                    id = moduleId, name = "OhMyKeymint", author = "", version = "", versionCode = 0,\n'
        '                    description = "", enabled = true, update = false, remove = false, updateJson = "",\n'
        '                    hasWebUi = true, hasActionScript = false, metamodule = false,\n'
        '                    actionIconPath = null, webUiIconPath = null,\n'
        '                )\n'
        '            } else null\n'
        '        }'
    )
    s = s.replace(old, new, 1)

    old_dir = 'webUIState.modDir = "/data/adb/modules/${moduleId}"'
    new_dir = ('webUIState.modDir = if (moduleId == "oh_my_keymint") '
               '"/data/adb/ksu/builtin/$moduleId" else "/data/adb/modules/$moduleId"')
    if old_dir in s:
        s = s.replace(old_dir, new_dir, 1)
    io.open(WH, 'w', encoding='utf-8').write(s)
    print('WebViewHelper patched')

def patch_manifest():
    s = io.open(MANIFEST, encoding='utf-8').read()
    if 'documentLaunchMode="intoExisting"' in s:
        s = s.replace('documentLaunchMode="intoExisting"', 'documentLaunchMode="always"')
        io.open(MANIFEST, 'w', encoding='utf-8').write(s)
        print('Manifest patched')
    else:
        print('Manifest no change')

if __name__ == '__main__':
    patch_webview_helper()
    patch_manifest()
