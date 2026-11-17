package me.weishu.kernelsu.ui.webui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.repository.ModuleRepositoryImpl
import me.weishu.kernelsu.ui.util.AppIconCache
import me.weishu.kernelsu.ui.util.createRootShell
import me.weishu.kernelsu.ui.util.withMainUserUid
import me.weishu.kernelsu.ui.viewmodel.SuperUserViewModel
import java.io.File

fun Activity.setTaskDescription(label: String) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        @Suppress("DEPRECATION")
        setTaskDescription(ActivityManager.TaskDescription(label))
    } else {
        val taskDescription = ActivityManager.TaskDescription.Builder()
            .setLabel(label)
            .build()
        setTaskDescription(taskDescription)
    }
}

@SuppressLint("SetJavaScriptEnabled")
internal suspend fun prepareWebView(
    activity: Activity,
    moduleId: String,
    webUIState: WebUIState,
    builtin: Boolean = false,
) {
    withContext(Dispatchers.IO) {
        val repo = ModuleRepositoryImpl()
        val modules = repo.getModules().getOrDefault(emptyList())
        // builtin 模块不在 /data/adb/modules 里，模块列表读不到，这里单独兜底构造
        // 非 root 进程读 /data/adb 恒 false，必须用 su 探测 builtin
        fun suExists(path: String): Boolean = try {
            createRootShell(false).use { sh ->
                val out = ArrayList<String>()
                sh.newJob("[ -d '$path' ] && echo 1 || echo 0").to(out).exec()
                out.firstOrNull()?.trim() == "1"
            }
        } catch (t: Throwable) { false }
        val moduleInfo = modules.find { info -> info.id == moduleId } ?: run {
            // builtin 直通：调用方明确标记了这是内置模块，不再依赖模块列表/文件探测
            if (builtin || suExists("/data/adb/ksu/builtin/$moduleId/webroot")) {
                me.weishu.kernelsu.data.model.Module(
                    id = moduleId,
                    name = moduleId,
                    author = "",
                    version = "",
                    versionCode = 0,
                    description = "",
                    enabled = if (builtin) true else !suExists("/data/adb/ksu/builtin/$moduleId/disable"),
                    update = false,
                    remove = false,
                    updateJson = "",
                    hasWebUi = true,
                    hasActionScript = false,
                    metamodule = false,
                    actionIconPath = null,
                    webUiIconPath = null,
                )
            } else {
                null
            }
        }

        if (moduleInfo == null) {
            withContext(Dispatchers.Main) {
                webUIState.uiEvent = WebUIEvent.Error(activity.getString(R.string.no_such_module, moduleId))
            }
            return@withContext
        }

        if (!moduleInfo.hasWebUi || !moduleInfo.enabled || moduleInfo.update || moduleInfo.remove) {
            withContext(Dispatchers.Main) {
                webUIState.uiEvent = WebUIEvent.Error(activity.getString(R.string.module_unavailable, moduleInfo.name))
            }
            return@withContext
        }

        webUIState.moduleName = moduleInfo.name
        // 同样用 su 判断（非 root 进程 File.exists 恒 false，会把 modules 模块误判成 builtin）
        webUIState.modDir = if (!builtin && suExists("/data/adb/modules/${moduleId}")) {
            "/data/adb/modules/${moduleId}"
        } else {
            "/data/adb/ksu/builtin/${moduleId}"
        }

        if (SuperUserViewModel.apps.isEmpty()) {
            SuperUserViewModel().fetchAppList()
        }
        val shell = createRootShell(true)
        webUIState.rootShell = shell

        withContext(Dispatchers.Main) {
            activity.setTaskDescription(activity.getString(R.string.app_name) + " - ${moduleInfo.name}")

            val webView = WebView(activity)
            webView.setBackgroundColor(Color.TRANSPARENT)

            val prefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE)
            WebView.setWebContentsDebuggingEnabled(prefs.getBoolean("enable_web_debugging", false))

            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
            }

            val webRoot = File("${webUIState.modDir}/webroot")
            val webViewAssetLoader = WebViewAssetLoader.Builder()
                .setDomain("mui.kernelsu.org")
                .addPathHandler(
                    "/",
                    SuFilePathHandler(
                        activity,
                        webRoot,
                        shell,
                        { webUIState.currentInsets },
                        { enable -> webUIState.isInsetsEnabled = enable })
                )
                .build()

            // WebViewClient
            webView.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val url = request.url
                    if (url.scheme.equals("ksu", ignoreCase = true) && url.host.equals("icon", ignoreCase = true)) {
                        val packageName = url.path?.substring(1)
                        if (!packageName.isNullOrEmpty()) {
                            val appInfo = SuperUserViewModel.apps
                                .find { it.packageName == packageName }
                                ?.packageInfo?.applicationInfo
                            if (appInfo != null) {
                                val icon = AppIconCache.loadIconSync(activity, appInfo.withMainUserUid(activity), 512)
                                val stream = java.io.ByteArrayOutputStream()
                                icon.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                                return WebResourceResponse(
                                    "image/png", null, 200, "OK",
                                    mapOf("Access-Control-Allow-Origin" to "*"),
                                    java.io.ByteArrayInputStream(stream.toByteArray())
                                )
                            } else {
                                val errorMsg = "No such package"
                                val errorStream = java.io.ByteArrayInputStream(errorMsg.toByteArray(Charsets.UTF_8))
                                return WebResourceResponse(
                                    "text/plain",
                                    "utf-8",
                                    404,
                                    "Not Found",
                                    mapOf("Access-Control-Allow-Origin" to "*"),
                                    errorStream
                                )
                            }
                        }
                    }
                    return webViewAssetLoader.shouldInterceptRequest(url)
                }

                override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                    webUIState.webCanGoBack = view?.canGoBack() ?: false
                    if (webUIState.isInsetsEnabled) webUIState.webView?.evaluateJavascript(webUIState.currentInsets.js, null)
                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }

            // WebChromeClient
            webView.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    if (message == null || result == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowAlert(message, result)
                    return true
                }

                override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                    if (message == null || result == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowConfirm(message, result)
                    return true
                }

                override fun onJsPrompt(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    defaultValue: String?,
                    result: JsPromptResult?
                ): Boolean {
                    if (message == null || result == null || defaultValue == null) return false
                    webUIState.uiEvent = WebUIEvent.ShowPrompt(message, defaultValue, result)
                    return true
                }

                override fun onShowFileChooser(
                    webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?
                ): Boolean {
                    webUIState.filePathCallback?.onReceiveValue(null)
                    webUIState.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*" }
                    if (fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE) {
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }
                    webUIState.uiEvent = WebUIEvent.ShowFileChooser(intent)
                    return true
                }
            }

            // JS Interface
            val webviewInterface = WebViewInterface(webUIState)
            webUIState.webView = webView
            webView.addJavascriptInterface(webviewInterface, "ksu")
            webUIState.uiEvent = WebUIEvent.WebViewReady
        }
    }
}