package me.weishu.kernelsu.ui.navigation3

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.net.toUri
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.weishu.kernelsu.Natives
import me.weishu.kernelsu.R
import me.weishu.kernelsu.data.repository.SettingsRepositoryImpl
import me.weishu.kernelsu.data.script.ScriptRepository
import me.weishu.kernelsu.ksuApp
import me.weishu.kernelsu.ui.component.dialog.rememberConfirmDialog
import me.weishu.kernelsu.ui.screen.flash.FlashIt
import me.weishu.kernelsu.ui.screen.scriptrun.ScriptRunArg
import me.weishu.kernelsu.ui.util.DownloadService
import me.weishu.kernelsu.ui.util.getFileName
import me.weishu.kernelsu.ui.webui.WebUIActivity

private const val SCHEME_KSU = "ksu"
private const val HOST_ACTION = "action"
private const val HOST_WEBUI = "webui"
private const val PARAM_ID = "id"
private const val PARAM_TOKEN = "token"

/**
 * Resolved intent action to execute after validation.
 */
private sealed interface PendingAction {
    /** Install module(s) from URI — triggered by DownloadService notification or ZIP file open. */
    data class InstallModule(
        val uri: Uri,
        val displayName: String,
        val requiresConfirmation: Boolean,
    ) : PendingAction {
        companion object {
            val InstallModuleSaver = listSaver<InstallModule?, Any>(
                save = { module ->
                    if (module == null) {
                        emptyList()
                    } else {
                        listOf(
                            module.uri,
                            module.displayName,
                            module.requiresConfirmation
                        )
                    }
                },
                restore = { list ->
                    if (list.isEmpty()) {
                        null
                    } else {
                        InstallModule(
                            uri = list[0] as Uri,
                            displayName = list[1] as String,
                            requiresConfirmation = list[2] as Boolean
                        )
                    }
                }
            )
        }
    }

    /** Execute a module's action script — triggered by shortcut or deep link. */
    data class ExecuteAction(val moduleId: String) : PendingAction

    /** Open a module's WebUI — triggered by shortcut. */
    data class OpenWebUI(val moduleId: String) : PendingAction

    /** 其他应用「用本 App 打开」文件：ACTION_VIEW / ACTION_SEND */
    data class OpenFile(val uri: Uri) : PendingAction
}

private sealed interface KsuDeepLink {
    data class Action(val moduleId: String) : KsuDeepLink
    data class WebUi(val moduleId: String) : KsuDeepLink
}

private fun buildInternalWebUiUri(moduleId: String): Uri {
    return Uri.Builder()
        .scheme(SCHEME_KSU)
        .authority(HOST_WEBUI)
        .appendQueryParameter(PARAM_ID, moduleId)
        .build()
}

/**
 * content:// → 真实文件路径。有 MANAGE_EXTERNAL_STORAGE 时能直接定位原文件，
 * 解析失败才退回「复制到 cache/incoming」。
 */
private fun resolveRealPath(context: android.content.Context, uri: Uri): String? {
    if (uri.scheme == "file") return uri.path
    // ExternalStorageProvider：content://com.android.externalstorage.documents/document/primary%3ADownload%2Fx.apk
    if (uri.authority?.contains("externalstorage") == true) {
        val path = try {
            val docId = android.provider.DocumentsContract.getDocumentId(uri)
            val parts = docId.split(":", limit = 2)
            if (parts.size == 2) {
                val base = if (parts[0].equals("primary", true)) "/storage/emulated/0" else "/storage/" + parts[0]
                base + "/" + parts[1]
            } else null
        } catch (t: Throwable) {
            null
        }
        if (path != null) return path
    }
    // MediaStore：查 _data
    val mediaPath = try {
        var p: String? = null
        val proj = arrayOf(android.provider.MediaStore.MediaColumns.DATA)
        context.contentResolver.query(uri, proj, null, null, null)?.use { c ->
            if (c.moveToFirst()) p = c.getString(0)
        }
        p
    } catch (t: Throwable) {
        null
    }
    if (!mediaPath.isNullOrBlank()) return mediaPath
    // DownloadsProvider
    if (uri.authority == "com.android.providers.downloads.documents") {
        val dlPath = try {
            val id = android.provider.DocumentsContract.getDocumentId(uri)
            "/storage/emulated/0/Download/" + id.substringAfterLast("/")
        } catch (t: Throwable) {
            null
        }
        if (dlPath != null) return dlPath
    }
    return null
}

fun getDisplayName(uri: Uri): String {
    return uri.getFileName(ksuApp) ?: uri.lastPathSegment ?: "Unknown"
}

/**
 * Resolve an intent snapshot into a [PendingAction].
 * Returns null if the intent carries no recognized action.
 */
private fun resolveIntent(intent: Intent): PendingAction? {
    // DownloadService notification: install module
    if (intent.action == DownloadService.ACTION_INSTALL_MODULE) {
        val token = intent.getStringExtra(DownloadService.EXTRA_TOKEN)?.takeIf { it.isNotBlank() } ?: return null
        if (token != SettingsRepositoryImpl().intentToken) return null
        val uriString = intent.getStringExtra(DownloadService.EXTRA_MODULE_URI)
            ?: return null
        val uri = uriString.toUri()
        return PendingAction.InstallModule(
            uri = uri,
            displayName = getDisplayName(uri),
            requiresConfirmation = false,
        )
    }

    // 其他应用「用我们的软件打开」：ACTION_VIEW / ACTION_SEND（content:// 或 file://）
    if (intent.action == Intent.ACTION_VIEW || intent.action == Intent.ACTION_SEND) {
        @Suppress("DEPRECATION")
        val shared = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val uri = intent.data ?: shared
        if (uri != null && uri.scheme != SCHEME_KSU) {
            return PendingAction.OpenFile(uri)
        }
    }

    // Check deep links
    return when (val deepLink = parseValidatedDeepLink(intent.data)) {
        is KsuDeepLink.Action -> PendingAction.ExecuteAction(deepLink.moduleId)
        is KsuDeepLink.WebUi -> PendingAction.OpenWebUI(deepLink.moduleId)
        null -> null
    }
}

private fun parseValidatedDeepLink(uri: Uri?): KsuDeepLink? {
    if (uri?.scheme != SCHEME_KSU) return null

    val moduleId = uri.getQueryParameter(PARAM_ID)?.takeIf { it.isNotBlank() } ?: return null
    val token = uri.getQueryParameter(PARAM_TOKEN)?.takeIf { it.isNotBlank() } ?: return null
    if (token != SettingsRepositoryImpl().intentToken) return null

    return when (uri.host) {
        HOST_ACTION -> KsuDeepLink.Action(moduleId)
        HOST_WEBUI -> KsuDeepLink.WebUi(moduleId)
        else -> null
    }
}

@SuppressLint("StringFormatInvalid")
@Composable
fun IntentDispatcher(intentChannel: ReceiveChannel<Intent>) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val navigator = LocalNavigator.current
    val isSafeMode = Natives.isSafeMode
    val isManager = Natives.isManager
    var pendingZipInstall by rememberSaveable(stateSaver = PendingAction.InstallModule.InstallModuleSaver) { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    // A one-shot script ZIP opens the run page, which also offers installing it as a module.
    val openModuleZip: (Uri) -> Unit = { uri ->
        scope.launch {
            val script = ScriptRepository.peekOneshot(uri)
            if (script != null) {
                navigator.push(Route.ScriptRun(ScriptRunArg(uri, offerInstall = true, displayName = script.name)))
            } else {
                navigator.push(Route.Flash(FlashIt.FlashModules(listOf(uri))))
            }
        }
    }

    val installDialog = rememberConfirmDialog(
        onConfirm = {
            pendingZipInstall?.let { action -> openModuleZip(action.uri) }
            pendingZipInstall = null
        },
        onDismiss = { pendingZipInstall = null }
    )

    CollectIntentChannel(intentChannel) { intent ->
        if (!isManager) return@CollectIntentChannel
        val action = resolveIntent(intent) ?: return@CollectIntentChannel

        when (action) {
            is PendingAction.InstallModule -> {
                if (isSafeMode) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.safe_mode_module_disabled),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@CollectIntentChannel
                }
                if (action.requiresConfirmation) {
                    pendingZipInstall = action
                    installDialog.showConfirm(
                        title = resources.getString(R.string.module),
                        content = resources.getString(
                            R.string.module_install_prompt_with_name,
                            "\n${action.displayName}"
                        )
                    )
                } else {
                    openModuleZip(action.uri)
                }
            }

            is PendingAction.ExecuteAction -> {
                navigator.push(Route.ExecuteModuleAction(action.moduleId, fromShortcut = true))
            }

            is PendingAction.OpenWebUI -> {
                val webIntent = Intent(context, WebUIActivity::class.java)
                    .setData(buildInternalWebUiUri(action.moduleId))
                context.startActivity(webIntent)
            }

            is PendingAction.OpenFile -> {
                // 其他应用「用本 App 打开」：把文件接进来，丢进文件管理器的收件目录并定位
                scope.launch {
                    // 优先解析真实路径（全盘权限已开时直接定位原文件，和 MT 一样）
                    val realPath = withContext(kotlinx.coroutines.Dispatchers.IO) {
                        resolveRealPath(context, action.uri)
                    }
                    if (realPath != null && java.io.File(realPath).exists()) {
                        me.weishu.kernelsu.ui.screen.FileLocate.side = "L"
                        me.weishu.kernelsu.ui.screen.FileLocate.path = realPath
                        me.weishu.kernelsu.ui.screen.FileLocate.tick += 1
                        navigator.popUntil { it is Route.Main }
                        return@launch
                    }
                    val copied = withContext(kotlinx.coroutines.Dispatchers.IO) {
                        runCatching {
                            val name = getDisplayName(action.uri)
                            val dir = java.io.File(context.cacheDir, "incoming").apply { mkdirs() }
                            val dst = java.io.File(dir, name)
                            context.contentResolver.openInputStream(action.uri)?.use { ins ->
                                dst.outputStream().use { outs -> ins.copyTo(outs) }
                            } ?: throw IllegalStateException("无法读取该文件")
                            dst.absolutePath
                        }.getOrNull()
                    }
                    if (copied != null) {
                        me.weishu.kernelsu.ui.screen.FileLocate.side = "L"
                        me.weishu.kernelsu.ui.screen.FileLocate.path = copied
                        me.weishu.kernelsu.ui.screen.FileLocate.tick += 1
                        // 回到文件管理器主界面并定位
                        navigator.popUntil { it is Route.Main }
                    } else {
                        Toast.makeText(context, "打开失败：无法读取该文件", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

/**
 * Receive intents inside a [LaunchedEffect] tied to the channel identity.
 * Each emitted intent is processed exactly once; no activity.intent mutation needed.
 */
@Composable
private fun CollectIntentChannel(intentChannel: ReceiveChannel<Intent>, onIntent: suspend (Intent) -> Unit) {
    LaunchedEffect(intentChannel) {
        for (intent in intentChannel) {
            onIntent(intent)
        }
    }
}
