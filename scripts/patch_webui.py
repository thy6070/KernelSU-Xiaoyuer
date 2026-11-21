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
        # 新版本 WebViewHelper 可能换了写法；这时跳过 builtin 直达补丁即可（模块已在 modules）
        print('WebViewHelper: moduleInfo 行未找到，跳过 builtin 直达补丁')
        return
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



def patch_webui_error_log():
    """WebUI 请求拦截：写诊断日志 + SuFilePathHandler 读不到时用 SuFile(root) 兜底。
    oh_my_keymint 在部分设备上 SuFilePathHandler 返回 statusCode=0（空响应），
    原因是它用 Java 的 getCanonicalPath 判断路径，/data/adb 下解析失败。"""
    s = io.open(WH, encoding='utf-8').read()
    if 'webui.log' in s and 'SuFileInputStream.open' in s:
        print('WebUI intercept already patched')
        return
    old = "                    return webViewAssetLoader.shouldInterceptRequest(url)"
    new = """                    val resp = webViewAssetLoader.shouldInterceptRequest(url)
                    // 诊断：记录每个被拦截的请求和响应状态（排查 WebUI 打不开）
                    try {
                        java.io.File(activity.filesDir, "webui.log").appendText(
                            System.currentTimeMillis().toString() + " " + url.toString() +
                                " -> " + (resp?.statusCode?.toString() ?: "null") +
                                " " + (resp?.mimeType ?: "") + "\\n"
                        )
                    } catch (t: Throwable) {}
                    // 兜底：SuFilePathHandler 读不到（statusCode 0）时用 SuFile 直接读
                    if (url.host == WEB_DOMAIN && (resp == null || resp.statusCode == 0)) {
                        try {
                            val rel = url.path?.removePrefix("/") ?: ""
                            if (rel.isNotEmpty() && !rel.startsWith("internal/")) {
                                val f = java.io.File(webUIState.modDir + "/webroot", rel)
                                val suFile = com.topjohnwu.superuser.io.SuFile(f.absolutePath)
                                suFile.setShell(shell)
                                val ins = com.topjohnwu.superuser.io.SuFileInputStream.open(suFile)
                                val ext = rel.substringAfterLast('.', "").lowercase()
                                val mime = when (ext) {
                                    "html" -> "text/html"
                                    "js", "mjs" -> "application/javascript"
                                    "css" -> "text/css"
                                    "json" -> "application/json"
                                    "png" -> "image/png"
                                    "jpg", "jpeg" -> "image/jpeg"
                                    "svg" -> "image/svg+xml"
                                    "woff2" -> "font/woff2"
                                    "ttf" -> "font/ttf"
                                    else -> "application/octet-stream"
                                }
                                return WebResourceResponse(mime, null, ins)
                            }
                        } catch (t: Throwable) {}
                    }
                    return resp"""
    if old not in s:
        print('WARN: shouldInterceptRequest return not found, skip webui patch')
        return
    s = s.replace(old, new, 1)
    io.open(WH, 'w', encoding='utf-8').write(s)
    print('WebUI intercept patched (log + SuFile fallback)')

def patch_webview_ua():
    """部分模块的 WebUI 用 UA 里的 Chrome 版本判断 WebView 是否 >=120。
    设备 WebView 明明够，但 UA 版本号解析不到 120 时会自己显示"需要 WebView 120+"。
    这里把 WebView 的 UA 统一成 Chrome 134（只影响页面看到的 UA，不影响渲染）。"""
    s = io.open(WH, encoding='utf-8').read()
    if 'userAgentString' in s:
        print('WebView UA already patched')
        return
    old = """            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
            }"""
    new = """            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
                // 伪造 UA 的 Chrome 版本，骗过模块 WebUI 的 WebView 版本检查
                userAgentString =
                    "Mozilla/5.0 (Linux; Android 14; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36"
            }"""
    if old not in s:
        print('WARN: settings.apply block not found, skip UA patch')
        return
    s = s.replace(old, new, 1)
    io.open(WH, 'w', encoding='utf-8').write(s)
    print('WebView UA patched')


if __name__ == '__main__':
    patch_webview_helper()
    patch_webview_ua()
    patch_webui_error_log()
    patch_manifest()
