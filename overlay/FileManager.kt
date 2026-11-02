package me.weishu.kernelsu.ui.screen

import android.app.Activity
import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import me.weishu.kernelsu.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.ui.zIndex
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.PI
import kotlin.math.sin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

private const val BG_COLOR = 0xFF2B2B2B.toInt()
private const val TILE_COLOR = 0xFF3A3A3A.toInt()
private const val TEXT_COLOR = 0xFFEDEDED.toInt()
private const val FOLDER_ICON = 0xFFD6D6D6.toInt()
private const val FILE_ICON = 0xFF9A9A9A.toInt()

private val apkIconCache = ConcurrentHashMap<String, ImageBitmap?>()
private val apkIconMutex = Mutex()

data class ModItem(val id: String, val name: String, val version: String, val desc: String, val disabled: Boolean)

data class SuApp(val pkg: String, val label: String, val uid: Int, val allowed: Boolean, val icon: android.graphics.Bitmap? = null)

data class FsEntry(val name: String, val isDir: Boolean)

@Composable
fun FileManagerScreen(onBack: () -> Unit = {}, onNavigate: (String) -> Unit = {}) {
    val appCtx = LocalContext.current
    val view = LocalView.current

    LaunchedEffect(Unit) {
        if (appCtx.getSharedPreferences("yx_mode", 0).getBoolean("perf", false)) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                        "sh /data/adb/service.d/xy_perf.sh on >/dev/null 2>&1; " +
                        "kill \$(cat /data/local/tmp/xy_guard.pid 2>/dev/null) 2>/dev/null; sleep 0.3; " +
                        "setsid sh /data/adb/xy_guard.sh >/dev/null 2>&1 &")).waitFor()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    var leftPath by remember { mutableStateOf("/storage/emulated/0") }
    var rightPath by remember { mutableStateOf("/data") }

    var panel by remember { mutableStateOf(false) }
    var showTerminal by remember { mutableStateOf(false) }
    var termInput by remember { mutableStateOf("") }
    var termText by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<String?>(null) }
    var refreshTick by remember { mutableStateOf(0) }
    var leftHighlight by remember { mutableStateOf<String?>(null) }
    var longPressPath by remember { mutableStateOf<String?>(null) }
    var longPressIsDir by remember { mutableStateOf(false) }
    var longPressTick by remember { mutableStateOf(0) }
    var forceSoPanelTick by remember { mutableStateOf(0) }
    var shareFailTick by remember { mutableStateOf(0) }
    var longPressFromRight by remember { mutableStateOf(false) }
    var editPath by remember { mutableStateOf<String?>(null) }
    var editText by remember { mutableStateOf("") }
    LaunchedEffect(FileLocate.tick) {
        val target = FileLocate.path ?: return@LaunchedEffect
        val dir = target.substringBeforeLast('/', "/")
        if (leftPath == dir) refreshTick += 1 else leftPath = dir
        leftHighlight = target.substringAfterLast('/')
    }

    var runMessage by remember { mutableStateOf<String?>(null) }
    var runOk by remember { mutableStateOf(true) }
    var running by remember { mutableStateOf(false) }
    var activeProc by remember { mutableStateOf<Process?>(null) }
    val panelP = remember { Animatable(0f) }
    val uiScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(BG_COLOR))) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(BG_COLOR))
                .blur(if (panel) 30.dp else 0.dp)
        ) {
    Row(modifier = Modifier.fillMaxSize()) {
        key(leftPath) {
            BrowserPane(
                path = leftPath,
                onNavigate = { leftPath = it },
                onLongPress = { lp ->
                    longPressIsDir = runCatching { java.io.File(lp).isDirectory }.getOrDefault(false) ||
                        runCatching {
                            val pr = Runtime.getRuntime().exec(
                                arrayOf(suBin(), "-c", "test -d '" + lp.replace("'", "'\\''") + "' && echo y")
                            )
                            val o = pr.inputStream.bufferedReader().readText()
                            pr.waitFor()
                            o.trim() == "y"
                        }.getOrDefault(false)
                    longPressFromRight = false
                    // 长按新目标时先收起终端，避免叠在一起
                    showTerminal = false
                    termText = ""
                    longPressPath = lp
                    longPressTick += 1
                },
                onFileClick = { picked ->
                    if (longPressPath != null) longPressPath = null
                    if (selectedFile != null && selectedFile != picked) {
                        selectedFile = null
                        uiScope.launch {
                            kotlinx.coroutines.delay(280)
                            selectedFile = picked
                        }
                    } else {
                        selectedFile = picked
                    }
                },
                refreshTick = refreshTick,
                onRefresh = { refreshTick += 1 },
                selectedPath = selectedFile,
                highlightName = leftHighlight,
                modifier = Modifier.weight(1f)
            )
        }
        Column(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF4A4A4A))) {}
        key(rightPath) {
            BrowserPane(
                path = rightPath,
                onNavigate = { rightPath = it },
                onLongPress = { lp ->
                    longPressIsDir = runCatching { java.io.File(lp).isDirectory }.getOrDefault(false) ||
                        runCatching {
                            val pr = Runtime.getRuntime().exec(
                                arrayOf(suBin(), "-c", "test -d '" + lp.replace("'", "'\\''") + "' && echo y")
                            )
                            val o = pr.inputStream.bufferedReader().readText()
                            pr.waitFor()
                            o.trim() == "y"
                        }.getOrDefault(false)
                    longPressFromRight = true
                    // 长按新目标时先收起终端，避免叠在一起
                    showTerminal = false
                    termText = ""
                    longPressPath = lp
                    longPressTick += 1
                },
                onFileClick = { picked ->
                    if (longPressPath != null) longPressPath = null
                    if (selectedFile != null && selectedFile != picked) {
                        selectedFile = null
                        uiScope.launch {
                            kotlinx.coroutines.delay(280)
                            selectedFile = picked
                        }
                    } else {
                        selectedFile = picked
                    }
                },
                refreshTick = refreshTick,
                onRefresh = { refreshTick += 1 },
                selectedPath = selectedFile,
                modifier = Modifier.weight(1f)
            )
        }
    }

    val uiCtx = androidx.compose.ui.platform.LocalContext.current
    ProMaxIsland(
        onNavigate = onNavigate,
        panelOpen = panel,
        selectedFile = selectedFile,
        onClearSelection = { selectedFile = null },
        onGoLeft = { pth -> leftPath = pth },
        onGoRight = { pth -> rightPath = pth },
        onExtractApp = { pkg, label ->
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    try {
                        val safe = label.replace("/", "_").replace("\"", "_")
                        val cmd = "P=\$(pm path $pkg | head -1 | sed 's/package://'); " +
                            "mkdir -p \"/storage/emulated/0/小鱼儿\"; " +
                            "cp \"\$P\" \"/storage/emulated/0/小鱼儿/$safe.apk\" && " +
                            "chmod 644 \"/storage/emulated/0/小鱼儿/$safe.apk\""
                        ProcessBuilder(suBin(), "-c", cmd).redirectErrorStream(true).start().waitFor()
                    } catch (t: Throwable) {
                    }
                }
                refreshTick += 1
            }
        },
        onOpenStorage = { leftPath = "/storage/emulated/0" },
        onOpenData = { rightPath = "/data" },
        onExtract = {
            val from = selectedFile
            if (from != null) {
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            Shell.cmd("cp -rf \"$from\" \"$leftPath/\" 2>/dev/null").exec()
                        } catch (t: Throwable) {
                        }
                    }
                    refreshTick += 1
                }
            }
        },
        onDelete = {
            val p = selectedFile
            if (p != null) uiScope.launch {
                withContext(Dispatchers.IO) { Shell.cmd("rm -rf \"$p\"").exec() }
                selectedFile = null
                refreshTick += 1
            }
        },
        runMessage = runMessage,
        runOk = runOk,
        running = running,
        terminalMode = showTerminal,
        termText = termText,
        termInput = termInput,
        onTermInput = { termInput = it },
        onTermRun = {
            val cmd = termInput
            // 允许空输入（发送换行给等待中的脚本）
            if (true) {
                termInput = ""
                val alive = activeProc
                if (alive != null && alive.isAlive) {
                    try {
                        alive.outputStream.write((cmd + "\n").toByteArray())
                        alive.outputStream.flush()
                        termText = termText + cmd + "\n"
                    } catch (t: Throwable) {
                        termText = termText + "写入失败: " + (t.message ?: "") + "\n"
                    }
                } else {
                    termText = termText + "\n$ " + cmd + "\n"
                    uiScope.launch {
                        val out = withContext(Dispatchers.IO) { Shell.cmd(cmd).exec() }
                        termText = termText + out.out.joinToString("\n") + out.err.joinToString("\n") + "\n"
                    }
                }
            }
        },
        onTermClose = { showTerminal = false },
        onArchiveEnter = {
            val p = longPressPath ?: selectedFile
            if (p != null) {
                uiScope.launch {
                    running = true
                    showTerminal = true
                    termText = ""
                    runMessage = null
                    withContext(Dispatchers.IO) {
                        try {
                            val q = "'" + p.replace("'", "'\''") + "'"
                            Shell.cmd(
                                "rm -rf /data/local/tmp/yx_arch && mkdir -p /data/local/tmp/yx_arch && " +
                                    "( unzip -o -q " + q + " -d /data/local/tmp/yx_arch 2>&1 || " +
                                    "tar -xf " + q + " -C /data/local/tmp/yx_arch 2>&1 )"
                            ).exec()
                        } catch (t: Throwable) {
                        }
                    }
                    running = false
                    showTerminal = false
                    leftPath = "/data/local/tmp/yx_arch"
                    refreshTick += 1
                }
            }
        },
        longPressPath = longPressPath,
        longPressIsDir = longPressIsDir,
        longPressTick = longPressTick,
        forceSoPanelTick = forceSoPanelTick,
        shareFailTick = shareFailTick,
        onClearLongPress = { longPressPath = null },
        oppositePath = if (longPressFromRight) leftPath else rightPath,
        currentPath = if (longPressFromRight) rightPath else leftPath,
        leftPath = leftPath,
        rightPath = rightPath,
        onFileOp = { op, arg ->
            val srcPath = longPressPath ?: selectedFile
            if (srcPath != null) {
                uiScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            fun q(s: String) = "'" + s.replace("'", "'\''") + "'"
                            val dir = srcPath.substringBeforeLast('/', "/")
                            val name = srcPath.substringAfterLast('/')
                            val cmd = when (op) {
                                "move" -> "mv -f " + q(srcPath) + " " + q(arg)
                                "copy" -> "cp -rf " + q(srcPath) + " " + q(arg)
                                "zip" -> "cd " + q(dir) + " && tar -czf " + q(name + ".tar.gz") + " " + q(name)
                                "unzip" -> "unzip -o " + q(srcPath) + " -d " + q(arg)
                                "rename" -> "mv -f " + q(srcPath) + " " + q(dir + "/" + arg)
                                "delete" -> "rm -rf " + q(srcPath)
                                "install" -> "cp -f " + q(srcPath) + " /data/local/tmp/xy_install.apk && chmod 644 /data/local/tmp/xy_install.apk && pm install -r -d -g /data/local/tmp/xy_install.apk 2>&1 | tail -n 3"
                                "edit" -> "chmod 644 " + q(srcPath)
                                else -> ""
                            }
                            var ok = true
                            if (cmd.isNotEmpty()) {
                                val proc = ProcessBuilder(suBin(), "-c", cmd)
                                    .redirectErrorStream(true).start()
                                val txt = proc.inputStream.bufferedReader().readText()
                                proc.waitFor()
                                ok = true
                                if (op == "install") ok = txt.contains("Success")
                            }
                            if (op == "share") {
                                // verbatim from the ZySU source: private cache dir works with FileProvider
                                val shareDir = java.io.File(appCtx.cacheDir, "share")
                                shareDir.mkdirs()
                                val dest = java.io.File(shareDir, java.io.File(srcPath).name)
                                try {
                                    dest.delete()
                                } catch (t: Throwable) {
                                }
                                runCatching {
                                    ProcessBuilder(
                                        suBin(), "-c",
                                        "cp " + q(srcPath) + " " + q(dest.absolutePath) +
                                            " ; chmod 644 " + q(dest.absolutePath)
                                    ).redirectErrorStream(true).start().waitFor()
                                }
                                var note: String? = null
                                if (!dest.exists()) {
                                    note = "提取失败"
                                } else {
                                    val candidates = listOf(appCtx.packageName + ".fileprovider")
                                    var launched = false
                                    for (authority in candidates) {
                                        if (launched) break
                                        try {
                                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                                appCtx, authority, dest
                                            )
                                            val send = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                type = "application/octet-stream"
                                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                                putExtra(android.content.Intent.EXTRA_TITLE, java.io.File(srcPath).name)
                                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                val chooser = android.content.Intent.createChooser(send, "分享")
                                                chooser.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                appCtx.startActivity(chooser)
                                            }
                                            launched = true
                                        } catch (t: Throwable) {
                                            note = t.message
                                        }
                                    }
                                }
                                if (note != null) {
                                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        runMessage = "分享失败：" + note
                                    }
                                }
                            }
                            if (op == "edit") {
                                val content = try {
                                    java.io.File(srcPath).readText()
                                } catch (t1: Throwable) {
                                    try {
                                        Shell.cmd("cat " + q(srcPath)).exec().out.joinToString("\n")
                                    } catch (t: Throwable) {
                                        ""
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    editPath = srcPath
                                    editText = content
                                }
                            }
                        } catch (t: Throwable) {
                        }
                    }
                    try {
                        Shell.getShell().close()
                    } catch (t: Throwable) {
                    }
                    refreshTick += 1
                    longPressPath = null
                    if (op == "delete") selectedFile = null   // 删除后清掉选中，否则快捷条还挂着
                    runOk = true
                    runMessage = if (op == "share") null else ("已完成")
                }
            }
        },
        onArchiveMount = {
            val p = selectedFile
            if (p != null) {
                uiScope.launch {
                    running = true
                    termText = ""
                    runMessage = null
                    showTerminal = true
                    val ksud = appCtx.applicationInfo.nativeLibraryDir + "/libksud.so"
                    val q = "'" + p.replace("'", "'\''") + "'"
                    val cmd = "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                        "export KSU_VER=1.0.0 ; export KSU_VER_CODE=12100 ; export KSU_KERNEL_VER_CODE=12100 ; " +
                        "cp -f " + q + " /data/local/tmp/xy_mod.zip 2>/dev/null ; " +
                        "'" + ksud + "' module install /data/local/tmp/xy_mod.zip 2>&1"
                    try {
                        val bb = listOf(
                            "/data/adb/ksu/bin/busybox",
                            "/data/adb/magisk/busybox",
                            "/system/bin/busybox",
                            "/system/xbin/busybox",
                            "/data/adb/modules/busybox-ndk/system/bin/busybox"
                        ).firstOrNull { java.io.File(it).isFile }
                        val qq = { s: String -> "'" + s.replace("'", "'\''") + "'" }
                        // 照原版：直接执行（不用 busybox script）；输出仍逐行喂给灵动岛终端
                        val wrapped = cmd + " 2>&1"
                        withContext(Dispatchers.IO) {
                            val pb = ProcessBuilder("/system/bin/sh", "-c", "su -c " + qq(wrapped))
                            pb.redirectErrorStream(true)
                            val proc = pb.start()
                            activeProc = proc
                            proc.inputStream.bufferedReader().useLines { lines ->
                                lines.forEach { line ->
                                    withContext(Dispatchers.Main) {
                                        termText = (termText + line + "\n").takeLast(20000)
                                    }
                                }
                            }
                            proc.waitFor()
                        }
                    } catch (t: Throwable) {
                        termText += "挂载失败: " + (t.message ?: "") + "\n"
                    }
                    activeProc = null
                    running = false
                    try {
                        Shell.getShell().close()
                    } catch (t: Throwable) {
                    }
                }
            }
        },
        onAction = { soKey ->
            val p = longPressPath ?: selectedFile
            val apkName = p?.substringAfterLast('/') ?: ""
            if (p != null && isApk(apkName)) {
                selectedFile = null
                uiScope.launch {
                    runMessage = "安装中…"
                    val msg = withContext(Dispatchers.IO) {
                        try {
                            val q = "'" + p.replace("'", "'\''") + "'"
                            val out = Shell.cmd(
                                "cp -f " + q + " /data/local/tmp/xy_install.apk; " +
                                    "chmod 644 /data/local/tmp/xy_install.apk; " +
                                    "pm install -r -d -g /data/local/tmp/xy_install.apk 2>&1 | tail -n 3"
                            ).exec()
                            val txt = (out.out + out.err).joinToString("\n")
                            if (txt.contains("Success")) "已安装" else "安装失败"
                        } catch (t: Throwable) {
                            "安装失败：" + (t.message ?: "")
                        }
                    }
                    runMessage = msg
                    refreshTick += 1
                }
            } else if (p != null) {
                val lower = p.lowercase()
                uiScope.launch {
                    running = true
                    termText = ""
                    runMessage = null
                    showTerminal = true
                    try {
                    withContext(Dispatchers.IO) {
                        try {
                            val dir = p.substringBeforeLast('/', "/")
                            val file = p.substringAfterLast('/')
                            val cmd = when {
                                lower.endsWith(".apk") ->
                                    "cp -f " + shq(p) + " /data/local/tmp/xy_install.apk ; " +
                                        "chmod 644 /data/local/tmp/xy_install.apk ; " +
                                        "pm install -r /data/local/tmp/xy_install.apk"
                                lower.endsWith(".so") -> {
                                    val key = soKey.trim()
                                    "cp -f \"$p\" /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                        "chmod 755 /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                        "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                                        (if (key.isEmpty()) "/data/local/tmp/xy_exec.so" else "/data/local/tmp/xy_exec.so -k \"$key\"") +
                                        " 2>&1 ; rm -f /data/local/tmp/xy_exec.so"
                                }
                                else -> "cp -f \"$p\" /data/local/tmp/xy_exec.sh 2>/dev/null ; " +
                                    "chmod 755 /data/local/tmp/xy_exec.sh 2>/dev/null ; " +
                                    "export TERM=xterm-256color ; " +
                                    "BB=/data/adb/ksu/bin/busybox ; " +
                                    "if [ -x \"\$BB\" ]; then \"\$BB\" script -q -c 'sh /data/local/tmp/xy_exec.sh' /dev/null 2>&1 ; " +
                                    "else sh /data/local/tmp/xy_exec.sh 2>&1 ; fi ; " +
                                    "rm -f /data/local/tmp/xy_exec.sh"
                            }
                            val pb = ProcessBuilder(suBin(), "-c", cmd)
                            pb.redirectErrorStream(true)
                            pb.directory(java.io.File("/data/local/tmp"))
                            pb.environment()["PATH"] = "/system/bin:/system/xbin:/sbin:/vendor/bin"
                            pb.environment()["HOME"] = "/data"
                            pb.environment()["TMPDIR"] = "/data/local/tmp"
                            val proc = pb.start()
                            withContext(Dispatchers.Main) { activeProc = proc }
                            val inp = proc.inputStream
                            val buf = ByteArray(4096)
                            while (true) {
                                val n = inp.read(buf)
                                if (n < 0) break
                                if (n > 0) {
                                    val chunk = String(buf, 0, n)
                                    withContext(Dispatchers.Main) {
                                        termText = termText + chunk
                                    }
                                }
                            }
                            proc.waitFor()
                        } catch (t: Throwable) {
                            withContext(Dispatchers.Main) {
                                termText = termText + "执行失败: " + (t.message ?: "") + "\n"
                            }
                        }
                    }
                    } finally {
                        running = false
                    }
                    refreshTick += 1
                }
            }
        }
    ) {
        uiScope.launch {
            panelP.snapTo(0f)
            panel = true
            panelP.animateTo(1f, tween(420, easing = FastOutSlowInEasing))
        }
    }
        }

        if (panel) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val s = 0.25f + 0.75f * panelP.value
                        scaleX = s
                        scaleY = s
                        alpha = panelP.value
                        transformOrigin = TransformOrigin(0.735f, 0.075f)
                    }
            ) {
                ModePanel(
                    onRunExec = { path ->
                        if (path.endsWith(".so")) {
                            selectedFile = path
                            forceSoPanelTick += 1
                            return@ModePanel
                        }
                        uiScope.launch {
                            running = true
                            termText = ""
                            runMessage = null
                            showTerminal = true
                            withContext(Dispatchers.IO) {
                                try {
                                    fun q3(s: String) = "'" + s.replace("'", "'\\''") + "'"
                                    val cmd = "export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                        "cd " + q3(java.io.File(path).parent ?: "/") + " 2>/dev/null; " +
                                        "chmod 755 " + q3(path) + " 2>/dev/null; " +
                                        "sh " + q3(path) + " 2>&1"
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", cmd))
                                    activeProc = pr
                                    pr.inputStream.bufferedReader().useLines { ls ->
                                        ls.forEach { line ->
                                            withContext(Dispatchers.Main) {
                                                termText = (termText + line + "\n").takeLast(20000)
                                            }
                                        }
                                    }
                                    pr.waitFor()
                                } catch (t: Throwable) {
                                    withContext(Dispatchers.Main) {
                                        termText += "执行失败: " + (t.message ?: "") + "\n"
                                    }
                                }
                            }
                            activeProc = null
                            running = false
                        }
                    },
                    onDismiss = {
                    uiScope.launch {
                        panelP.animateTo(0f, tween(280, easing = androidx.compose.animation.core.FastOutSlowInEasing))
                        panel = false
                    }
                })
            }
        }
    if (editPath != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "编辑 " + (editPath ?: "").substringAfterLast('/'),
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                androidx.compose.foundation.text.BasicTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF141418), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        IslandActionButton("取消", Color(0xFF3A3A3C)) {
                            editPath = null
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        IslandActionButton("保存", Color(0xFF0A84FF)) {
                            val target = editPath
                            if (target != null) {
                                uiScope.launch {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            val b64 = android.util.Base64.encodeToString(
                                                editText.toByteArray(),
                                                android.util.Base64.NO_WRAP
                                            )
                                            Shell.cmd(
                                                "echo '" + b64 + "' | base64 -d > '" +
                                                    target.replace("'", "'\''") + "'"
                                            ).exec()
                                        } catch (t: Throwable) {
                                        }
                                    }
                                    editPath = null
                                    refreshTick += 1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    }
}

@OptIn(ExperimentalFoundationApi::class)
/** Render ANSI SGR colors (MT-style) inside our terminal panel. */
private fun ansiAnnotated(src: String): androidx.compose.ui.text.AnnotatedString {
    val b = androidx.compose.ui.text.AnnotatedString.Builder()
    var color = Color.White
    var i = 0
    while (i < src.length) {
        val c = src[i]
        if (c == '\u001B') {
            // ESC 序列：CSI(ESC[)、OSC(ESC])、其他
            if (i + 1 < src.length) {
                val n = src[i + 1]
                if (n == '[') {
                    // CSI：ESC [ 参数 最终字节(0x40-0x7E)
                    var j = i + 2
                    while (j < src.length && src[j] !in '\u0040'..'\u007E') j++
                    if (j < src.length) {
                        val fin = src[j]
                        val params = src.substring(i + 2, j)
                        if (fin == 'm') {
                            when (params) {
                                "0", "" -> color = Color.White
                                "31" -> color = Color(0xFFFF3B30)
                                "32" -> color = Color(0xFF34C759)
                                "33" -> color = Color(0xFFFFCC00)
                                "34" -> color = Color(0xFF0A84FF)
                                "35" -> color = Color(0xFFFF2D95)
                                "36" -> color = Color(0xFF32ADE6)
                                "37" -> color = Color.White
                                "91" -> color = Color(0xFFFF6961)
                                "92" -> color = Color(0xFF34C759)
                                "93" -> color = Color(0xFFFFD60A)
                                "94" -> color = Color(0xFF0A84FF)
                                "95" -> color = Color(0xFFFF2D95)
                                "96" -> color = Color(0xFF32ADE6)
                            }
                        }
                        i = j + 1
                        continue
                    }
                    i = src.length
                    continue
                } else if (n == ']') {
                    // OSC：ESC ] ... BEL 或 ESC \
                    var j = i + 2
                    while (j < src.length) {
                        if (src[j] == '\u0007') { j++; break }
                        if (src[j] == '\u001B' && j + 1 < src.length && src[j + 1] == '\\') { j += 2; break }
                        j++
                    }
                    i = j
                    continue
                } else {
                    // 其他 ESC 序列（2 字符）
                    i += 2
                    continue
                }
            }
            i++
            continue
        }
        if (c == '\r') { i++; continue }   // 回车忽略
        // 普通字符（按当前颜色）
        val runStart = i
        while (i < src.length && src[i] != '\u001B' && src[i] != '\r') i++
        b.pushStyle(androidx.compose.ui.text.SpanStyle(color = color))
        b.append(src.substring(runStart, i))
        b.pop()
    }
    return b.toAnnotatedString()
}

@Composable
private fun ProMaxIsland(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    panelOpen: Boolean,
    selectedFile: String?,
    runMessage: String?,
    runOk: Boolean,
    running: Boolean,
    terminalMode: Boolean,
    termText: String,
    termInput: String,
    onTermInput: (String) -> Unit,
    onTermRun: () -> Unit,
    onTermClose: () -> Unit,
    onClearSelection: () -> Unit,
    onGoLeft: (String) -> Unit,
    onGoRight: (String) -> Unit,
    onOpenStorage: () -> Unit,
    onOpenData: () -> Unit,
    onExtract: () -> Unit,
    onExtractApp: (String, String) -> Unit,
    onDelete: () -> Unit,
    onAction: (String) -> Unit,
    onArchiveEnter: () -> Unit,
    onArchiveMount: () -> Unit,
    longPressPath: String?,
    longPressIsDir: Boolean,
    longPressTick: Int,
    forceSoPanelTick: Int,
    shareFailTick: Int,
    onClearLongPress: () -> Unit,
    oppositePath: String,
    currentPath: String = "/storage/emulated/0/",
    leftPath: String = "/storage/emulated/0/",
    rightPath: String = "/data/",
    onFileOp: (String, String) -> Unit,
    onOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showWave by remember { mutableStateOf(false) }
    var navMode by remember { mutableStateOf(false) }
    var navTarget by remember { mutableStateOf<String?>(null) }
    var navClosing by remember { mutableStateOf(false) }
    var jumpStep by remember { mutableStateOf(0) }
    var jumpSide by remember { mutableStateOf(0) }
    var jumpInput by remember { mutableStateOf("") }
    var renameInput by remember { mutableStateOf("") }
    val ctxNav = androidx.compose.ui.platform.LocalContext.current
    var susfsVer by remember { mutableStateOf("") }
    var susfsVariant by remember { mutableStateOf("") }
    var susfsSupp by remember { mutableStateOf(false) }
    var zygiskOn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        susfsSupp = try {
            "Unsupported" == "Supported"
        } catch (t: Throwable) {
            false
        }
        susfsVer = try {
            ""
        } catch (t: Throwable) {
            ""
        }
        susfsVariant = try {
            ""
        } catch (t: Throwable) {
            ""
        }
        zygiskOn = false
    }
    var paneTarget by remember(currentPath) { mutableStateOf(currentPath.trimEnd('/') + "/") }
    var createIsDir by remember { mutableStateOf(true) }
    var extractDone by remember { mutableStateOf(false) }
    var rootOk by remember { mutableStateOf(true) }
    var rootChecked by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (true) {
            val ok = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching { suNow("id 2>/dev/null").contains("uid=0") }.getOrDefault(false)
            }
            rootOk = ok
            rootChecked = true
            kotlinx.coroutines.delay(6000)
        }
    }

    var modules by remember { mutableStateOf<List<ModItem>>(emptyList()) }
    var modSel by remember { mutableStateOf(-1) }
    var suApps by remember { mutableStateOf<List<SuApp>>(emptyList()) }
    var revokedPkgs by remember { mutableStateOf<Set<String>>(emptySet()) }
    var suSel by remember { mutableStateOf(-1) }
    val navCtx = LocalContext.current
    val navScope = rememberCoroutineScope()
    LaunchedEffect(navTarget, jumpStep) {
        modSel = -1
        suSel = -1
        if (navTarget == "module") {
            modules = withContext(Dispatchers.IO) {
                try {
                    val ids = Shell.cmd("ls -1 /data/adb/modules 2>/dev/null").exec().out
                        .filter { it.isNotBlank() }
                    ids.mapNotNull { id ->
                        val prop = Shell.cmd("cat /data/adb/modules/$id/module.prop 2>/dev/null").exec().out
                        if (prop.isEmpty()) null else {
                            val map = prop.mapNotNull { line ->
                                val i = line.indexOf('=')
                                if (i > 0) line.substring(0, i).trim() to line.substring(i + 1).trim() else null
                            }.toMap()
                            val disabled = Shell.cmd("test -e /data/adb/modules/$id/disable && echo yes").exec()
                                .out.joinToString("").contains("yes")
                            ModItem(
                                id = id,
                                name = map["name"] ?: id,
                                version = map["version"] ?: "",
                                desc = map["description"] ?: "",
                                disabled = disabled
                            )
                        }
                    }
                } catch (t: Throwable) {
                    emptyList()
                }
            }
        }
        if (navTarget == "su" || (navTarget == "home" && jumpStep == 7)) {
            val base = withContext(Dispatchers.IO) {
                try {
                    val repo = me.weishu.kernelsu.data.repository.SuperUserRepositoryImpl()
                    val res = repo.getAppList()
                    val apps = res.getOrNull()?.first ?: emptyList()
                    val pm = navCtx.packageManager
                    apps.filter { ai ->
                        if (ai.packageName == navCtx.packageName) return@filter false
                        if (ai.allowSu) return@filter true
                        ai.packageInfo.applicationInfo?.let {
                            (it.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0
                        } ?: true
                    }.map { ai ->
                        val icon = try {
                            val d = ai.packageInfo.applicationInfo?.loadIcon(pm) ?: pm.getApplicationIcon(ai.packageName)
                            val w = d.intrinsicWidth.coerceAtLeast(1)
                            val h = d.intrinsicHeight.coerceAtLeast(1)
                            val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                            val canvas = android.graphics.Canvas(bmp)
                            d.setBounds(0, 0, w, h)
                            d.draw(canvas)
                            bmp
                        } catch (t: Throwable) {
                            null
                        }
                        SuApp(ai.packageName, ai.label, ai.uid, ai.profile?.allowSu == true, icon)
                    }
                } catch (t: Throwable) {
                    emptyList()
                }
            }
            suApps = base.sortedByDescending { it.allowed }

        }
    }
    val bootCtx = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                val prefs = bootCtx.getSharedPreferences("yx_mode", 0)
                if (prefs.getBoolean("perf", false)) {
                    Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                        "sh /data/adb/service.d/xy_perf.sh >/dev/null 2>&1")).waitFor()
                }
                if (prefs.getBoolean("open", false)) {
                    Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                        "sh /data/adb/service.d/xy_open_access.sh >/dev/null 2>&1")).waitFor()
                }
            }
        }
    }
    var soInput by remember { mutableStateOf("") }
    var soPanel by remember { mutableStateOf(false) }
    // 打开面板时把密钥文件第二行读出来填进输入框（用 su 读，App 无权限）
    LaunchedEffect(soPanel) {
        if (soPanel) {
            val k = withContext(Dispatchers.IO) {
                try {
                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                        "sed -n '2p' '/storage/emulated/0/小鱼儿/小鱼儿密钥' 2>/dev/null"))
                    val o = pr.inputStream.bufferedReader().readText().trim()
                    pr.waitFor()
                    o.ifEmpty { null }
                } catch (t: Throwable) {
                    null
                }
            }
            if (!k.isNullOrEmpty()) soInput = k
        }
    }
    LaunchedEffect(selectedFile) {
        if (selectedFile != null) {
            // 选中新文件时，先收起确认删除等步骤面板，避免两层重叠
            navMode = false
            navTarget = null
            navClosing = false
        }
        soPanel = false
    }
    LaunchedEffect(extractDone) {
        if (extractDone) {
            kotlinx.coroutines.delay(2200)
            extractDone = false
        }
    }
    LaunchedEffect(runMessage) {
        if (runMessage == "安装中…" || runMessage == "已安装" ||
            runMessage == "安装失败" || runMessage == "已完成") {
            expanded = false
        }
    }
    LaunchedEffect(running) {
        if (running) {
            showWave = true
        } else {
            kotlinx.coroutines.delay(650)
            showWave = false
        }
    }
    val reopenScope = rememberCoroutineScope()
    fun foldAndReopen() {
        reopenScope.launch {
            expanded = false
            kotlinx.coroutines.delay(280)
            expanded = true
        }
    }

    fun closeIsland() {
        reopenScope.launch {
            navClosing = true
            navMode = false
            expanded = false
            onClearLongPress()
            kotlinx.coroutines.delay(180)
            navTarget = null
            jumpStep = 0
            jumpInput = ""
            navClosing = false
        }
    }

    fun switchStep(target: Int) {
        reopenScope.launch {
            navClosing = true
            kotlinx.coroutines.delay(110)
            jumpStep = target
            navMode = true
            navTarget = "home"
            navClosing = false
            expanded = true
        }
    }

    LaunchedEffect(longPressPath) {
        // a cleared long press (file tapped elsewhere) must fold the menu
        if (longPressPath == null && jumpStep == 9) {
            closeIsland()
        }
    }
    LaunchedEffect(shareFailTick) {
        if (shareFailTick > 0) {
            expanded = true
        }
    }
    LaunchedEffect(forceSoPanelTick) {
        if (forceSoPanelTick > 0) soPanel = true
    }
    LaunchedEffect(longPressTick) {
        if (longPressTick <= 0) return@LaunchedEffect
        val lp = longPressPath ?: return@LaunchedEffect
        if (lp.isNotEmpty()) {
            navClosing = true
            expanded = false
            jumpStep = 0
            kotlinx.coroutines.delay(110)
            navMode = true
            navTarget = "home"
            jumpStep = 9
            renameInput = lp.substringAfterLast('/')
            navClosing = false
            expanded = true
        }
    }

    fun openArchive() {
        reopenScope.launch {
            navClosing = true
            kotlinx.coroutines.delay(110)
            navMode = true
            navTarget = "home"
            jumpStep = 8
            navClosing = false
            expanded = true
        }
    }

    fun openNav(target: String?) {
        reopenScope.launch {
            // 打开导航/步骤面板前先收起终端，避免重叠
            onTermClose()
            navClosing = true
            kotlinx.coroutines.delay(150)
            navMode = true
            navTarget = target
            navClosing = false
            expanded = true
        }
    }

    // Island.kt motion: 1s bump/sway/lift envelope
    val bounce = remember { Animatable(0f) }
    LaunchedEffect(expanded, selectedFile) {
        bounce.snapTo(0f)
        bounce.animateTo(1f, tween(1000))
    }
    val bt = bounce.value
    val bump = 1f + 0.06f * sin(bt * PI.toFloat())
    val sway = 6f * sin(bt * 9.424778f) * (1f - bt)
    val lift = -4f * sin(bt * PI.toFloat())

    // Island.kt ball choreography
    val wingPress = remember { Animatable(0f) }
    LaunchedEffect(panelOpen) {
        if (panelOpen) {
            wingPress.animateTo(1f, tween(220, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)))
        } else {
            wingPress.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 420f))
        }
    }
    val ball = remember { Animatable(1f) }
    val wingHidden = selectedFile != null || navMode || terminalMode
    LaunchedEffect(wingHidden) {
        if (wingHidden) {
            ball.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 120f))
        } else {
            ball.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 130f))
        }
    }
    val ballShift = ball.value
    val ballScale = 0.55f + 0.45f * ballShift

    val config = LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val pillW = (config.screenWidthDp * 0.36f).dp

    val sel = selectedFile
    val selName = sel?.substringAfterLast('/') ?: ""
    val accent = sel?.let { fileAccent(it) } ?: Color(0xFFEDEDED)
    val lowerSel = sel?.lowercase() ?: ""
    val actionLabel = when {
        sel == null -> ""
        isApk(selName) -> "安装"
        lowerSel.endsWith(".so") -> "启动"
        lowerSel.endsWith(".sh") || lowerSel.endsWith(".rc") -> "执行"
        lowerSel.endsWith(".zip") || lowerSel.endsWith(".tar") || lowerSel.endsWith(".gz") ||
            lowerSel.endsWith(".7z") || lowerSel.endsWith(".rar") || lowerSel.endsWith(".xz") -> "更多"
        else -> "编辑"
    }
    val actionColor = Color(0xFF0A84FF)

    val zyEase = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)


    Box(
        modifier = Modifier.fillMaxWidth().offset(y = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // wing sits UNDER the pill and slides out of it
        Row(
            modifier = Modifier
                .offset(x = (pillW.value / 2f + 30f).dp, y = 0.dp)
                .width(52.dp)
                .height(42.dp)
                .graphicsLayer {
                    val travel = with(density) { (pillW.value / 2f + 3f).dp.toPx() }
                    val press = 1f - 0.16f * wingPress.value
                    translationX = (ballShift - 1f) * travel
                    scaleX = ballScale * bump * press
                    scaleY = ballScale * bump * press
                    alpha = (ballShift * 3f).coerceIn(0f, 1f)
                }
                .clip(RoundedCornerShape(21.dp))
                .background(Color.Black)
                .border(0.5.dp, Color(0x47FFFFFF), RoundedCornerShape(21.dp))
                .clickable { onOpen() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = me.weishu.kernelsu.R.mipmap.ic_modules_foreground),
                contentDescription = null,
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White),
                modifier = Modifier.size(30.dp)
            )
        }

        // main pill on top
        Column(
            modifier = Modifier
                .zIndex(1f)
                .width(pillW)
                .graphicsLayer {
                    scaleX = bump
                    scaleY = bump
                    translationX = with(density) { sway.dp.toPx() }
                    translationY = with(density) { lift.dp.toPx() }
                }
                .clip(RoundedCornerShape(34.dp))
                .background(Color.Black)
                .border(0.5.dp, Color(0x47FFFFFF), RoundedCornerShape(34.dp))
                .combinedClickable(
                    onClick = {
                        if (terminalMode) onTermClose()
                        // let the panels shrink before the pill settles, so closing keeps its animation
                        val hadNav = navMode || navTarget != null
                        navMode = false
                        expanded = false
                        onClearLongPress()
                        if (hadNav) {
                            navClosing = true
                            reopenScope.launch {
                                kotlinx.coroutines.delay(180)
                                navTarget = null
                                jumpStep = 0
                                jumpInput = ""
                                navClosing = false
                            }
                        }
                        soPanel = false
                        if (sel != null) onClearSelection()
                        expanded = false
                        reopenScope.launch {
                            kotlinx.coroutines.delay(300)
                            jumpStep = 0
                            jumpInput = ""
                        }
                    },
                    onLongClick = { openNav(null) }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(start = 12.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val logoCtx = LocalContext.current
                val logoBmp = remember(logoCtx) {
                    val d = logoCtx.applicationInfo.loadIcon(logoCtx.packageManager)
                    val w = d.intrinsicWidth.coerceAtLeast(1)
                    val h = d.intrinsicHeight.coerceAtLeast(1)
                    val bmp = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bmp)
                    d.setBounds(0, 0, w, h)
                    d.draw(canvas)
                    bmp.asImageBitmap()
                }
                Image(
                    bitmap = logoBmp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(percent = 22))
                )
                if (extractDone) {
                    Text(
                        text = "已完成",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF4CAF50)
                    )
                }
                if (terminalMode || (running && expanded)) {
                    YukiWave(
                        color = Color(0xFFFFB6C8),
                        running = running,
                        modifier = Modifier.weight(1f)
                    )
                } else if (sel != null || longPressPath != null) {
                    Text(
                        text = if (longPressPath != null) longPressPath.substringAfterLast('/') else selName,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = if (runMessage != null && !runOk) Icons.Outlined.Lock else Icons.Outlined.InsertDriveFile,
                        contentDescription = null,
                        tint = if (runMessage != null && !runOk) Color(0xFFFF3B30) else accent,
                        modifier = Modifier.size(22.dp).clickable { onClearSelection() }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (!rootChecked || rootOk) Icons.Outlined.TaskAlt
                                      else Icons.Outlined.Lock,
                        contentDescription = if (rootChecked && !rootOk) "root unavailable" else "install",
                        tint = if (rootChecked && !rootOk) Color(0xFFFF3B30) else Color.White,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onNavigate("install") }
                    )
                }
            }

            AnimatedVisibility(
                visible = (navMode || navTarget != null) && !navClosing,
                enter = expandVertically(animationSpec = tween(260, easing = zyEase), expandFrom = Alignment.Bottom) + fadeIn(tween(160)),
                exit = shrinkVertically(animationSpec = tween(260, easing = zyEase), shrinkTowards = Alignment.Bottom) + fadeOut(tween(40))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = tween(260, easing = zyEase))
                ) {
                if (navTarget == null) Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavIcon(Icons.Outlined.InsertDriveFile) { openNav("home") }
                    NavIcon(Icons.Outlined.AdminPanelSettings) { openNav("su") }
                    NavIcon(Icons.Outlined.Layers) { openNav("module") }
                    NavIcon(Icons.Outlined.Tune) {
                        expanded = true
                        navMode = true
                        navTarget = "tweaks"
                    }
                }
            }

                if (navTarget != null) Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when (navTarget) {
                        "tweaks" -> {
                            var softReboot by remember { mutableStateOf(false) }
                            var selinuxHide by remember { mutableStateOf(false) }
                            var kernelUmount by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                withContext(Dispatchers.IO) {
                                    softReboot = navCtx.getSharedPreferences("settings", 0).getBoolean("soft_reboot", false)
                                    try {
                                        fun q(c: String): String {
                                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                            val o = pr.inputStream.bufferedReader().readText()
                                            pr.waitFor()
                                            return o
                                        }
                                        selinuxHide = q("/data/adb/ksud feature get selinux_hide 2>/dev/null").contains("Value: 1")
                                        kernelUmount = q("/data/adb/ksud feature get kernel_umount 2>/dev/null").contains("Value: 1")
                                    } catch (t: Throwable) {
                                    }
                                }
                            }
                            fun setFeature(name: String, v: Boolean) {
                                navScope.launch {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                                "/data/adb/ksud feature set $name ${if (v) 1 else 0}; /data/adb/ksud feature save")).waitFor()
                                        } catch (t: Throwable) {
                                        }
                                    }
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            softReboot = !softReboot
                                            navCtx.getSharedPreferences("settings", 0).edit()
                                                .putBoolean("soft_reboot", softReboot).apply()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "使用软重启代替重启\n安装、更新或开关模块时使用软重启代替完整重启",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "开", fontSize = 12.sp,
                                            color = if (softReboot) Color.White else Color(0xFF5A5A5E))
                                        Text(text = " | ", fontSize = 12.sp, color = Color(0xFF5A5A5E))
                                        Text(text = "关", fontSize = 12.sp,
                                            color = if (softReboot) Color(0xFF5A5A5E) else Color.White)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            selinuxHide = !selinuxHide
                                            setFeature("selinux_hide", selinuxHide)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "隐藏 SELinux 修改\n阻止应用检测 SELinux 修改",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "开", fontSize = 12.sp,
                                            color = if (selinuxHide) Color.White else Color(0xFF5A5A5E))
                                        Text(text = " | ", fontSize = 12.sp, color = Color(0xFF5A5A5E))
                                        Text(text = "关", fontSize = 12.sp,
                                            color = if (selinuxHide) Color(0xFF5A5A5E) else Color.White)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            kernelUmount = !kernelUmount
                                            setFeature("kernel_umount", kernelUmount)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "卸载模块（内核级）\n在内核给需要的应用卸载模块",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "开", fontSize = 12.sp,
                                            color = if (kernelUmount) Color.White else Color(0xFF5A5A5E))
                                        Text(text = " | ", fontSize = 12.sp, color = Color(0xFF5A5A5E))
                                        Text(text = "关", fontSize = 12.sp,
                                            color = if (kernelUmount) Color(0xFF5A5A5E) else Color.White)
                                    }
                                }
                            }
                        }
                        "module" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                    .padding(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (modules.isEmpty()) {
                                        Text(text = "没有添加模块", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                    } else {
                                        modules.forEachIndexed { mi, m ->
                                            val chosen = modSel == mi
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                                    .background(
                                                        if (chosen) Color(0xFF00BCD4) else Color(0x33FFFFFF),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .padding(1.dp)
                                                    .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                    .clickable { modSel = if (chosen) -1 else mi }
                                                    .padding(horizontal = 10.dp, vertical = 7.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = m.name,
                                                        fontSize = 12.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color.White,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Text(
                                                        text = if (m.disabled) "已禁用" else "启用",
                                                        fontSize = 10.sp,
                                                        color = if (m.disabled) Color(0xFFFF9800) else Color(0xFF4CAF50)
                                                    )
                                                }
                                                if (m.version.isNotEmpty()) {
                                                    Text(text = m.version, fontSize = 10.sp, color = Color(0xFF9E9E9E))
                                                }
                                                if (m.desc.isNotEmpty()) {
                                                    Text(
                                                        text = m.desc,
                                                        fontSize = 10.sp,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color(0xFF9E9E9E)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (modSel >= 0 && modSel < modules.size) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        IslandActionButton("删除", Color(0xFFFF3B30)) {
                                            val mod = modules.getOrNull(modSel)
                                            if (mod != null) {
                                                navScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        try {
                                                            // 与官方一致：走 ksud 正规卸载流程
                                                            Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                                                "/data/adb/ksud module uninstall \"${mod.id}\""))
                                                                .waitFor()
                                                        } catch (t: Throwable) {
                                                        }
                                                    }
                                                    modules = modules.filterNot { it.id == mod.id }
                                                    modSel = -1
                                                }
                                            }
                                        }
                                    }
                                    Box(modifier = Modifier.weight(1f)) {
                                        IslandActionButton("操作", Color(0xFF0A84FF)) {
                                            val mod = modules.getOrNull(modSel)
                                            if (mod != null) {
                                                navScope.launch {
                                                    val hasWeb = withContext(Dispatchers.IO) {
                                                        try {
                                                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                                                "test -d \"/data/adb/modules/${mod.id}/webroot\" && echo yes"))
                                                            val o = pr.inputStream.bufferedReader().readText()
                                                            pr.waitFor()
                                                            o.contains("yes")
                                                        } catch (t: Throwable) {
                                                            false
                                                        }
                                                    }
                                                    if (hasWeb) {
                                                        try {
                                                            val token = me.weishu.kernelsu.data.repository.SettingsRepositoryImpl().intentToken
                                                            val it = android.content.Intent(
                                                                navCtx,
                                                                me.weishu.kernelsu.ui.webui.WebUIActivity::class.java
                                                            ).apply {
                                                                putExtra("is_internal_transition", true)
                                                                setData(android.net.Uri.parse("ksu://webui?id=${mod.id}&token=$token"))
                                                                putExtra("id", mod.id)
                                                                putExtra("name", mod.name)
                                                            }
                                                            navCtx.startActivity(it)
                                                        } catch (t: Throwable) {
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                                                    "sh \"/data/adb/modules/${mod.id}/action.sh\" 2>/dev/null")).waitFor()
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "su" -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                    .padding(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (suApps.isEmpty()) {
                                        Text(text = "没有应用", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                    } else {
                                        suApps.forEachIndexed { si, app ->
                                            val chosen = suSel == si
                                            val ring = when {
                                                chosen -> Color(0xFF00BCD4)
                                                app.allowed -> Color(0xFFFF6FA8)
                                                else -> Color(0x33FFFFFF)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 6.dp)
                                                    .background(ring, RoundedCornerShape(10.dp))
                                                    .padding(1.dp)
                                                    .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                    .clickable { suSel = if (chosen) -1 else si }
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val iconBmp = app.icon
                                                if (iconBmp != null) {
                                                    Image(
                                                        bitmap = iconBmp.asImageBitmap(),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                }
                                                Text(
                                                    text = app.label,
                                                    fontSize = 12.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = Color.White,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                if (app.allowed) {
                                                    Text(text = "已寄生", fontSize = 10.sp, color = Color(0xFFFF6FA8))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (suSel >= 0 && suSel < suApps.size) {
                                val app = suApps[suSel]
                                IslandActionButton(
                                    if (app.allowed) "撤销" else "寄生",
                                    if (app.allowed) Color(0xFFFF3B30) else Color(0xFF0A84FF)
                                ) {
                                    navScope.launch {
                                        withContext(Dispatchers.IO) {
                                            try {
                                                // same path as the AppProfile screen: read the live
                                                // profile, flip allowSu, write it back
                                                val current = me.weishu.kernelsu.Natives.getAppProfile(app.pkg, app.uid)
                                                val next = current?.copy(allowSu = !app.allowed)
                                                    ?: me.weishu.kernelsu.Natives.Profile(
                                                        name = app.pkg,
                                                        currentUid = app.uid,
                                                        allowSu = !app.allowed
                                                    )
                                                me.weishu.kernelsu.Natives.setAppProfile(next)
                                            } catch (t: Throwable) {
                                            }
                                        }
                                        revokedPkgs = if (app.allowed) {
                                            revokedPkgs + app.pkg
                                        } else {
                                            revokedPkgs - app.pkg
                                        }
                                        suApps = suApps.mapIndexed { i, a ->
                                            if (i == suSel) a.copy(allowed = !a.allowed) else a
                                        }.sortedByDescending { it.allowed }
                                        suSel = -1
                                    }
                                }
                            }
                        }
                        "home" -> {
                            when (jumpStep) {
                                8 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("进入", Color(0xFF0A84FF)) {
                                                onArchiveEnter()
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("挂载", Color(0xFFFFB300)) {
                                                onArchiveMount()
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                9 -> {
                                    val lp = longPressPath ?: ""
                                    val lowerLp = lp.lowercase()
                                    val isArchive = lowerLp.endsWith(".zip") || lowerLp.endsWith(".tar") ||
                                        lowerLp.endsWith(".gz") || lowerLp.endsWith(".7z") ||
                                        lowerLp.endsWith(".rar") || lowerLp.endsWith(".xz")
                                    val isApkFile = isApk(lp.substringAfterLast('/'))
                                    val isExec = lowerLp.endsWith(".sh") || lowerLp.endsWith(".so") ||
                                        lowerLp.endsWith(".rc")
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        IslandActionButton("移动/复制", Color(0xFF0A84FF)) {
                                            switchStep(10)
                                        }
                                        IslandActionButton(
                                            if (isArchive) "解压" else "压缩",
                                            Color(0xFF0A84FF)
                                        ) {
                                            if (isArchive) onFileOp("unzip", oppositePath)
                                            else onFileOp("zip", "")
                                            closeIsland()
                                        }
                                        IslandActionButton("重命名", Color(0xFF0A84FF)) {
                                            switchStep(11)
                                        }
                                        IslandActionButton("复制路径", Color(0xFF0A84FF)) {
                                            try {
                                                val cm = ctxNav.getSystemService(
                                                    android.content.Context.CLIPBOARD_SERVICE
                                                ) as android.content.ClipboardManager
                                                cm.setPrimaryClip(
                                                    android.content.ClipData.newPlainText("path", lp)
                                                )
                                            } catch (t: Throwable) {
                                            }
                                            closeIsland()
                                        }
                                        if (longPressIsDir) {
                                            IslandActionButton("删除", Color(0xFFFF3B30)) {
                                                switchStep(12)
                                            }
                                        } else {
                                            IslandActionButton("分享", Color(0xFF0A84FF)) {
                                                onFileOp("share", "")
                                                closeIsland()
                                            }
                                        }
                                        if (!longPressIsDir) {
                                            IslandActionButton(
                                                when {
                                                    isApkFile -> "安装"
                                                    isArchive -> "进入"
                                                    isExec -> "编辑"
                                                    else -> "执行"
                                                },
                                                Color(0xFF0A84FF)
                                            ) {
                                                when {
                                                    isApkFile -> onFileOp("install", "")
                                                    isArchive -> onArchiveEnter()
                                                    isExec -> onFileOp("edit", "")
                                                    else -> onAction("")
                                                }
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                10 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("移动", Color(0xFF3A3A3C)) {
                                                onFileOp("move", oppositePath)
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("复制", Color(0xFF0A84FF)) {
                                                onFileOp("copy", oppositePath)
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                11 -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = renameInput,
                                        onValueChange = { renameInput = it },
                                        singleLine = true,
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("确定", Color(0xFF0A84FF)) {
                                                if (renameInput.isNotBlank()) {
                                                    onFileOp("rename", renameInput.trim())
                                                }
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                12 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("确认删除", Color(0xFFFF3B30)) {
                                                onFileOp("delete", "")
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                0 -> {
                                    GradientActionButton("提取", Color(0xFF0A84FF)) { switchStep(7) }
                                    GradientActionButton("data/储存", Color(0xFF0A84FF)) { switchStep(2) }
                                    GradientActionButton("文件/文件夹", Color(0xFF0A84FF)) { switchStep(1) }
                                    GradientActionButton("跳转", Color(0xFF0A84FF)) { switchStep(4) }
                                }
                                1 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("创建文件", Color(0xFF3A3A3C)) {
                                                createIsDir = false
                                                switchStep(6)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("创建文件夹", Color(0xFF0A84FF)) {
                                                createIsDir = true
                                                switchStep(6)
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("data", Color(0xFF3A3A3C)) {
                                                paneTarget = "/data/"
                                                switchStep(3)
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("储存", Color(0xFF0A84FF)) {
                                                paneTarget = "/storage/emulated/0/"
                                                switchStep(3)
                                            }
                                        }
                                    }
                                }
                                3 -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                onGoLeft(paneTarget)
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                onGoRight(paneTarget)
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                4 -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = jumpInput,
                                        onValueChange = { jumpInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        singleLine = true
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                onGoLeft(jumpInput)
                                                closeIsland()
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                onGoRight(jumpInput)
                                                closeIsland()
                                            }
                                        }
                                    }
                                }
                                7 -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .background(Color(0xFF0E0E10), RoundedCornerShape(12.dp))
                                            .padding(6.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .verticalScroll(rememberScrollState())
                                        ) {
                                            if (suApps.isEmpty()) {
                                                Text(text = "没有应用", fontSize = 11.sp, color = Color(0xFFBDBDBD))
                                            } else {
                                                suApps.forEach { app ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(bottom = 6.dp)
                                                            .background(Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                                                            .padding(1.dp)
                                                            .background(Color(0xFF16161A), RoundedCornerShape(10.dp))
                                                            .clickable {
                                                                onExtractApp(app.pkg, app.label)
                                                                jumpStep = 0
                                                                navTarget = null
                                                                navMode = false
                                                                expanded = false
                                                                extractDone = true
                                                            }
                                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val iconBmp = app.icon
                                                        if (iconBmp != null) {
                                                            Image(
                                                                bitmap = iconBmp.asImageBitmap(),
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                        }
                                                        Text(
                                                            text = app.label,
                                                            fontSize = 12.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            color = Color.White,
                                                            modifier = Modifier.weight(1f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = jumpInput,
                                        onValueChange = { jumpInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = Color.White,
                                            fontSize = 12.sp
                                        ),
                                        singleLine = true
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向左", Color(0xFF3A3A3C)) {
                                                val nm = jumpInput.trim()
                                                if (nm.isNotEmpty()) {
                                                    navScope.launch {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                val base = leftPath.trimEnd('/') + "/"
                                                                if (createIsDir) {
                                                                    Shell.cmd("mkdir -p \"$base$nm\"").exec()
                                                                } else {
                                                                    Shell.cmd("touch \"$base$nm\"").exec()
                                                                }
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                        onGoLeft(leftPath.trimEnd('/') + "/")
                                                        closeIsland()
                                                    }
                                                }
                                            }
                                        }
                                        Box(modifier = Modifier.weight(1f)) {
                                            IslandActionButton("向右", Color(0xFF0A84FF)) {
                                                val nm = jumpInput.trim()
                                                if (nm.isNotEmpty()) {
                                                    navScope.launch {
                                                        withContext(Dispatchers.IO) {
                                                            try {
                                                                val base = rightPath.trimEnd('/') + "/"
                                                                if (createIsDir) {
                                                                    Shell.cmd("mkdir -p \"$base$nm\"").exec()
                                                                } else {
                                                                    Shell.cmd("touch \"$base$nm\"").exec()
                                                                }
                                                            } catch (t: Throwable) {
                                                            }
                                                        }
                                                        onGoRight(rightPath.trimEnd('/') + "/")
                                                        closeIsland()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = terminalMode,
                enter = expandVertically(animationSpec = tween(360, easing = zyEase), expandFrom = Alignment.CenterVertically) + fadeIn(tween(200)),
                exit = shrinkVertically(animationSpec = tween(260, easing = zyEase), shrinkTowards = Alignment.CenterVertically) + fadeOut(tween(160))
            ) {
                val termScroll = rememberScrollState()
                    LaunchedEffect(termText) {
                        termScroll.animateScrollTo(termScroll.maxValue)
                    }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = ansiAnnotated(termText),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .verticalScroll(termScroll),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.foundation.text.BasicTextField(
                            value = termInput,
                            onValueChange = onTermInput,
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF0E0E10), RoundedCornerShape(6.dp))
                                .padding(4.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 11.sp
                            ),
                            singleLine = true
                        )
                        Text(
                            "发送",
                            modifier = Modifier
                                .clickable { onTermRun() }
                                .padding(start = 10.dp)
                                .background(Color(0xFF0A84FF), RoundedCornerShape(percent = 50))
                                .padding(horizontal = 16.dp, vertical = 7.dp),
                            fontSize = 14.sp,
                            color = Color.White
                        )

                    }
                }
            }

            AnimatedVisibility(
                visible = sel != null && !terminalMode && !navMode && !soPanel,
                enter = expandVertically(
                    animationSpec = tween(360, easing = zyEase),
                    expandFrom = Alignment.CenterVertically
                ) + fadeIn(tween(200)),
                exit = shrinkVertically(
                    animationSpec = tween(260, easing = zyEase),
                    shrinkTowards = Alignment.CenterVertically
                ) + fadeOut(tween(160))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            IslandActionButton("删除", Color(0xFFFF3B30)) { switchStep(12) }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            IslandActionButton(actionLabel, actionColor) {
                                if (actionLabel == "更多") openArchive()
                                else if (actionLabel == "编辑") onFileOp("edit", "")
                                else if (lowerSel.endsWith(".so")) soPanel = true
                                else onAction("")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = soPanel && sel != null && !terminalMode && !navMode,
                enter = expandVertically(
                    animationSpec = tween(320, easing = zyEase),
                    expandFrom = Alignment.CenterVertically
                ) + fadeIn(tween(180)),
                exit = shrinkVertically(
                    animationSpec = tween(240, easing = zyEase),
                    shrinkTowards = Alignment.CenterVertically
                ) + fadeOut(tween(140))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = soInput,
                        onValueChange = { soInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        ),
                        singleLine = true
                    )
                    IslandActionButton("启动", Color(0xFF0A84FF)) {
                        // 输入框有内容 -> 用 su 写入密钥文件第二行（App 无权限直接写）；空 -> 读第二行
                        navScope.launch {
                            val manual = soInput.trim()
                            val key = withContext(Dispatchers.IO) {
                                try {
                                    fun sq(x: String) = "'" + x.replace("'", "'\''") + "'"
                                    if (manual.isNotEmpty()) {
                                        // 只改第 2 行，保留 1/3/4/5（之前 printf 会截断后面几行）
                                        val cmd = "F='/storage/emulated/0/小鱼儿/小鱼儿密钥' ; mkdir -p '/storage/emulated/0/小鱼儿' ; " +
                                            "[ -f \"\$F\" ] || : > \"\$F\" ; " +
                                            "awk -v n=2 -v v=" + sq(manual) + " 'NR==n{print v; f=1; next} {print} END{if(!f) for(i=NR+1;i<=n;i++) print (i==n?v:\"\")}' \"\$F\" > /data/local/tmp/xy_kf2 && cat /data/local/tmp/xy_kf2 > \"\$F\" ; rm -f /data/local/tmp/xy_kf2 ; chmod 600 \"\$F\""
                                        Runtime.getRuntime().exec(arrayOf(suBin(), "-c", cmd)).waitFor()
                                        manual
                                    } else {
                                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                            "sed -n '2p' '/storage/emulated/0/小鱼儿/小鱼儿密钥' 2>/dev/null"))
                                        val o = pr.inputStream.bufferedReader().readText().trim()
                                        pr.waitFor()
                                        o.ifEmpty { null }
                                    }
                                } catch (t: Throwable) {
                                    manual.ifEmpty { null }
                                }
                            }
                            onAction(key ?: soInput)
                        }
                        soPanel = false
                    }
                }
            }
        }
    }
}

@Composable
private fun GradientActionButton(label: String, color: Color, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color, RoundedCornerShape(percent = 50))
            .padding(vertical = 11.dp),
        fontSize = 14.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun DarkActionButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF16161A), RoundedCornerShape(percent = 50))
            .border(0.5.dp, Color(0xFF3A3A3C), RoundedCornerShape(percent = 50))
            .padding(vertical = 11.dp),
        fontSize = 14.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun FileBigButton(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF16161A), RoundedCornerShape(26.dp))
            .border(0.5.dp, Color(0xFF3A3A3C), RoundedCornerShape(26.dp))
            .padding(vertical = 15.dp),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun NavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(percent = 50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun IslandActionButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(color, RoundedCornerShape(percent = 50))
            .padding(vertical = 11.dp),
        fontSize = 14.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center,
        maxLines = 1,
        color = Color.White
    )
}

@Composable
private fun TerminalOverlay(onExit: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val lines = remember { androidx.compose.runtime.mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }

    fun run(cmd: String) {
        if (cmd.isBlank()) return
        lines.add("> " + cmd)
        busy = true
        scope.launch {
            val out = withContext(Dispatchers.IO) { Shell.cmd(cmd).exec() }
            out.out.forEach { if (it.isNotBlank()) lines.add(it) }
            if (out.err.isNotEmpty()) out.err.forEach { if (it.isNotBlank()) lines.add(it) }
            busy = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xF2000000)).padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "终端", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "关闭",
                color = Color(0xFF0A84FF),
                fontSize = 13.sp,
                modifier = Modifier.clickable { onExit() }
            )
        }
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(lines) { line ->
                Text(
                    text = line,
                    color = Color(0xFFDDDDDD),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "\$", color = Color(0xFF4CAF50), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            androidx.compose.foundation.text.BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Go
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onGo = {
                        run(input)
                        input = ""
                    }
                )
            )
            Text(
                text = if (busy) "…" else "运行",
                color = Color(0xFF0A84FF),
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    run(input)
                    input = ""
                }
            )
        }
    }
}

@Composable
private fun YukiWave(color: Color, running: Boolean, modifier: Modifier = Modifier) {
    val phase = remember { Animatable(0f) }
    val fill = remember { Animatable(0f) }
    val finishing = remember { mutableStateOf(false) }
    val round = remember { mutableStateOf(0) }
    LaunchedEffect(running) {
        if (running) {
            fill.snapTo(0f)
            while (true) {
                phase.snapTo(0f)
                phase.animateTo(1f, tween(900, easing = LinearEasing))
                round.value = round.value + 1
            }
        } else {
            finishing.value = true
            phase.animateTo(1f, tween(450, easing = LinearEasing))
            phase.snapTo(0f)
            phase.animateTo(1f, tween(320, easing = LinearEasing))
            round.value = round.value + 1
            finishing.value = false
            fill.animateTo(1f, tween(340, easing = LinearEasing))
        }
    }
    Canvas(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(12.dp)
            .clipToBounds()
    ) {
        val cy = size.height / 2f
        drawLine(
            color = Color.White.copy(alpha = 0.45f),
            start = Offset(0f, cy),
            end = Offset(size.width, cy),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
        if (running || finishing.value) {
            val seg = size.width * (0.30f + 0.10f * kotlin.math.min(round.value, 4).toFloat())
            val amp = 2.6.dp.toPx()
            for (k in 0 until 2) {
                val p = (phase.value + k * 0.5f) % 1f
                val x0 = -seg + p * (size.width + seg)
                val path = Path()
                var x = 0f
                while (x <= seg) {
                    val px = x0 + x
                    val py = cy + amp * kotlin.math.sin(x / 26.dp.toPx() * 6.2831855f - phase.value * 6.2831855f)
                    if (x <= 0f) path.moveTo(px, py) else path.lineTo(px, py)
                    x += 1f
                }
                val ribbon = Brush.horizontalGradient(
                    0f to color.copy(alpha = 0.05f),
                    0.14f to color,
                    0.86f to color,
                    1f to color.copy(alpha = 0.05f)
                )
                drawPath(
                    path = path,
                    brush = ribbon,
                    alpha = 0.18f,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = path,
                    brush = ribbon,
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
        if (fill.value > 0.001f) {
            drawLine(
                color = color,
                start = Offset(0f, cy),
                end = Offset(size.width * fill.value.coerceIn(0f, 1f), cy),
                strokeWidth = 2.4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ModePanel(onDismiss: () -> Unit, onRunExec: (String) -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize().clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeButton(Icons.Outlined.DarkMode, "放开通行")
            ModeButton(Icons.Outlined.AcUnit, "性能模式")
            ModeButton(Icons.Outlined.Folder, "执行此文件", slot = "path3", onRun = {
                onDismiss()
                onRunExec(it)
            })
            ModeButton(Icons.Outlined.Folder, "执行此文件", slot = "path2", onRun = {
                onDismiss()
                onRunExec(it)
            })
            ModeButton(Icons.Outlined.Folder, "执行此文件", onRun = {
                onDismiss()
                onRunExec(it)
            })
        }
    }
}

@Composable
private fun ModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onRun: ((String) -> Unit)? = null,
    slot: String = "path"
) {
    var on by remember { mutableStateOf(false) }
    val ctxMsg = androidx.compose.ui.platform.LocalContext.current
    var latencyMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        latencyMsg = ctxMsg.getSharedPreferences("yx_mode", 0).getString("msg_" + title, null)
    }
    LaunchedEffect(latencyMsg) {
        val v = latencyMsg
        if (v != null) {
            ctxMsg.getSharedPreferences("yx_mode", 0).edit().putString("msg_" + title, v).apply()
        }
    }
    val ctx3 = androidx.compose.ui.platform.LocalContext.current
    val scope2 = rememberCoroutineScope()
    var execPath by remember { mutableStateOf<String?>(null) }
    var showPathDialog by remember { mutableStateOf(false) }
    var showKeyDialog by remember { mutableStateOf(false) }
    var showUrlDialog by remember { mutableStateOf(false) }
    var urlInput by remember { mutableStateOf("") }
    var pathInput by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }
    suspend fun refreshLatency() {
        latencyMsg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    fun measure(p: String): Double {
                        var best = Double.MAX_VALUE
                        for (r in 0 until 3) {
                            val iter = 8000
                            val t0 = System.nanoTime()
                            for (i in 0 until iter) {
                                try {
                                    android.system.Os.stat(p)
                                } catch (e: Throwable) {
                                }
                            }
                            val per = (System.nanoTime() - t0).toDouble() / iter
                            if (per < best) best = per
                        }
                        return best
                    }
                    val den = (measure("/data/system") + measure("/data/misc")) / 2.0
                    val mod = measure("/data/adb/modules")
                    if (mod - den > 500.0) "会拉闸" else "不会拉闸"
                } catch (t: Throwable) {
                    null
                }
            }
    }
    LaunchedEffect(title) {
        if (title == "性能模式") {
            val remembered = ctx3.getSharedPreferences("yx_mode", 0).getBoolean("perf", false)
            if (remembered) {
                scope2.launch {
                    val r = withContext(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                "sh /data/adb/service.d/xy_perf.sh on >/dev/null 2>&1; " +
                                "kill \$(cat /data/local/tmp/xy_guard.pid 2>/dev/null) 2>/dev/null; sleep 0.3; " +
                                "setsid sh /data/adb/xy_guard.sh >/dev/null 2>&1 & sleep 3; " +
                                "sh /data/adb/service.d/xy_check.sh 2>/dev/null | tail -1"))
                            val o = pr.inputStream.bufferedReader().readText().trim()
                            pr.waitFor()
                            o
                        } catch (t: Throwable) { "" }
                    }
                    latencyMsg = if (r.isNotBlank()) "已开启 · " + r else "已开启 · 守护运行中"
                }
            }
            on = remembered ||
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val pr = Runtime.getRuntime().exec(
                        arrayOf(suBin(), "-c", "[ -f /data/adb/service.d/xy_perf.sh ] && echo y || echo n")
                    )
                    val o = pr.inputStream.bufferedReader().readText()
                    pr.waitFor()
                    o.trim() == "y"
                } catch (t: Throwable) {
                    false
                }
            }
        }
        if (title == "执行此文件" || title == "刷入驱动") {
            execPath = withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val lineNo = when (slot) { "path" -> 3; "path2" -> 4; "path3" -> 5; else -> 3 }
                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                        "sed -n '" + lineNo + "p' '/storage/emulated/0/小鱼儿/小鱼儿密钥' 2>/dev/null"))
                    val o = pr.inputStream.bufferedReader().readText().trim()
                    pr.waitFor()
                    o.ifEmpty { null }
                } catch (t: Throwable) {
                    null
                }
            }
        }
        if (title == "隐藏BL") {
            on = ctx3.getSharedPreferences("yx_mode", 0).getBoolean("bl", false) ||
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    fun q(c: String): String {
                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                        val o = pr.inputStream.bufferedReader().readText()
                        pr.waitFor()
                        return o
                    }
                    val mod = q("grep -l '小鱼儿附属' /data/adb/modules/*/module.prop 2>/dev/null | head -n1").trim()
                    val vs = q("getprop ro.boot.verifiedbootstate 2>/dev/null").trim()
                    mod.isNotEmpty() || vs == "green"
                } catch (t: Throwable) {
                    false
                }
            }
        }
        if (title == "放开通行") {
            val remembered2 = ctx3.getSharedPreferences("yx_mode", 0).getBoolean("open", false)
            if (remembered2) {
                scope2.launch {
                    withContext(kotlinx.coroutines.Dispatchers.IO) {
                        runCatching {
                            Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                "sh /data/adb/service.d/xy_open_access.sh >/dev/null 2>&1")).waitFor()
                        }
                    }
                }
            }
            on = remembered2 ||
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val pr = Runtime.getRuntime().exec(
                        arrayOf(suBin(), "-c", "[ -f /data/adb/service.d/xy_open_access.sh ] && echo y || echo n")
                    )
                    val o = pr.inputStream.bufferedReader().readText()
                    pr.waitFor()
                    o.trim() == "y"
                } catch (t: Throwable) {
                    false
                }
            }
            refreshLatency()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xE60E0E10))
            .clickable {
                on = !on
                if (title == "刷入内核模块") {
                    latencyMsg = "导出中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun sh(c: String): String {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                    val o = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    return o
                                }
                                val rel = sh("uname -r 2>/dev/null").trim()
                                val ver = rel.substringBefore('-')
                                val mm = ver.split(".").take(2).joinToString(".")
                                val sdk = android.os.Build.VERSION.SDK_INT
                                val android = when {
                                    sdk >= 36 -> "android17"
                                    sdk >= 35 -> "android16"
                                    sdk >= 34 -> "android15"
                                    sdk >= 33 -> "android14"
                                    sdk >= 31 -> "android13"
                                    else -> "android12"
                                }
                                val candidates = listOf(
                                    android + "-" + mm + "_kernelsu.ko.bin",
                                    android + "-" + mm + "_susfs_guard_lkm.ko.bin"
                                )
                                val assets = ctx3.assets
                                val listed = assets.list("lkm")?.toList() ?: emptyList()
                                // prefer the file whose kmi matches both android level and kernel line
                                val pick = listed.firstOrNull { it.startsWith(android + "-" + mm) }
                                    ?: listed.firstOrNull { it.startsWith(android) && it.contains(mm) }
                                    ?: candidates.firstOrNull { it in listed }
                                if (pick == null) {
                                    "无匹配模块(" + android + "-" + mm + ")"
                                } else {
                                    val key = byteArrayOf(
                                        -89, 31, 60, -98, 91, 45, -124, 96, -7, -63, -25, -93,
                                        -43, -72, 32, 70, 19, -49, -98, 122, 91, 45, -124, 97,
                                        -16, -29, -55, -89, -75, -46, 70, 24
                                    )
                                    val raw = assets.open("lkm/" + pick).readBytes()
                                    val plain = ByteArray(raw.size) { i ->
                                        (raw[i].toInt() xor key[i % key.size].toInt()).toByte()
                                    }
                                    val dir = java.io.File("/storage/emulated/0/小鱼儿")
                                    dir.mkdirs()
                                    val out = java.io.File(dir, pick.removeSuffix(".bin"))
                                    out.writeBytes(plain)
                                    sh("chmod 644 '" + out.absolutePath.replace("'", "'\\''") + "'")
                                    "已导出 " + out.name
                                }
                            } catch (t: Throwable) {
                                "导出失败"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                ctx3.getSharedPreferences("yx_mode", 0).edit().putBoolean(
                    when (title) {
                        "性能模式" -> "perf"
                        "放开通行" -> "open"
                        "隐藏BL" -> "bl"
                        else -> "none"
                    },
                    on
                ).apply()
                if (title == "下载RT驱动") {
                    urlInput = ctx3.getSharedPreferences("yx_exec", 0).getString("rt_url", "") ?: ""
                    showUrlDialog = true
                }
                if (title == "执行此文件" && onRun != null) {
                    val savedPath = execPath
                    if (savedPath.isNullOrBlank()) {
                        latencyMsg = "暂无设置"
                    } else {
                        onRun(savedPath)
                    }
                } else if (title == "执行此文件") {
                    val path = execPath
                    if (path.isNullOrBlank()) {
                        latencyMsg = "暂无设置"
                    } else if (path.endsWith(".so")) {
                        keyInput = ""
                        showKeyDialog = true
                    } else {
                        latencyMsg = "执行中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    fun su2(c: String): String {
                                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                        val o = pr.inputStream.bufferedReader().readText()
                                        pr.waitFor()
                                        return o
                                    }
                                    val q = "'" + path.replace("'", "'\\''") + "'"
                                    val out = su2("export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                        "cd '" + java.io.File(path).parent + "' 2>/dev/null; " +
                                        "chmod 755 $q 2>/dev/null; sh $q 2>&1 | tail -n 2")
                                    (if (out.contains("fail") || out.contains("error")) "执行失败" else "已执行")
                                } catch (t: Throwable) {
                                    "执行异常"
                                }
                            }
                            latencyMsg = msg
                        }
                    }
                }
                if (title == "隐藏BL") {
                    val enabling = on
                    latencyMsg = "处理中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                // resetprop is what makes the props invisible to detection
                                if (enabling) {
                                    su("resetprop ro.boot.verifiedbootstate green 2>/dev/null; " +
                                        "resetprop ro.boot.flash.locked 1 2>/dev/null; " +
                                        "resetprop ro.boot.veritymode enforcing 2>/dev/null; " +
                                        "resetprop ro.secureboot.lockstate locked 2>/dev/null; " +
                                        "resetprop ro.boot.warranty_bit 0 2>/dev/null; true")
                                    // download through the app (Android has no curl), then hand it to ksud
                                    su("mkdir -p /data/local/tmp")
                                    try {
                                        val cacheZip = java.io.File(ctx3.cacheDir, "as.zip")
                                        if (!cacheZip.exists() || cacheZip.length() < 10000) {
                                            val conn = java.net.URL(
                                                "https://gitlab.com/api/v4/projects/87739512/packages/generic/AlwaysStrong/v1.0.5-r2/AlwaysStrong-v1.0.5.zip"
                                            ).openConnection() as java.net.HttpURLConnection
                                            conn.connectTimeout = 20000
                                            conn.readTimeout = 60000
                                            conn.instanceFollowRedirects = true
                                            conn.inputStream.use { input ->
                                                cacheZip.outputStream().use { out -> input.copyTo(out) }
                                            }
                                        }
                                        su("cp '" + cacheZip.absolutePath.replace("'", "'\\''") + "' /data/local/tmp/as.zip")
                                    } catch (t: Throwable) {
                                    }
                                    val ksud = ctx3.applicationInfo.nativeLibraryDir + "/libksud.so"
                                    su("'" + ksud.replace("'", "'\''") + "' module install /data/local/tmp/as.zip 2>&1")
                                    // rename it so the module list shows 小鱼儿附属
                                    su("for d in /data/adb/modules/*/; do " +
                                        "if [ -f \"\${d}module.prop\" ] && grep -qi 'alwaysstrong' \"\${d}module.prop\" 2>/dev/null; then " +
                                        "sed -i 's/^name=.*/name=小鱼儿附属/' \"\${d}module.prop\" 2>/dev/null; " +
                                        "sed -i 's/^description=.*/description=小鱼儿bl控制器/' \"\${d}module.prop\" 2>/dev/null; " +
                                        "fi; done; true")
val modDir = su("grep -l '小鱼儿附属' /data/adb/modules/*/module.prop 2>/dev/null | head -n1").trim()
                                    val vs = su("getprop ro.boot.verifiedbootstate 2>/dev/null").trim()
                                    val lk = su("getprop ro.boot.flash.locked 2>/dev/null").trim()
                                    val propOk = vs == "green" && lk == "1"
                                    val modOk = modDir.isNotEmpty()
                                    (if (modOk) "小鱼儿附属已装·重启" else "模块安装失败") +
                                        (if (propOk) " · BL✓" else " · BL✗")
                                } else {
                                    su("resetprop --delete ro.boot.verifiedbootstate 2>/dev/null; " +
                                        "resetprop --delete ro.boot.flash.locked 2>/dev/null; " +
                                        "resetprop --delete ro.boot.veritymode 2>/dev/null; " +
                                        "resetprop --delete ro.secureboot.lockstate 2>/dev/null; " +
                                        "resetprop --delete ro.boot.warranty_bit 2>/dev/null; true")
                                    su("for m in /data/adb/modules/AlwaysStrong /data/adb/modules/alwaysstrong; do [ -d \"\$m\" ] && touch \"\$m/remove\"; done 2>/dev/null; true")
                                    "已恢复 BL 状态"
                                }
                            } catch (t: Throwable) {
                                "操作失败"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                if (title == "性能模式") {
                    val enabling = on
                    latencyMsg = "处理中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                if (enabling) {
                                    val gameLines = listOf(
                    "#!/system/bin/sh",
                    "# 极道帝兵 · 游戏加速 v11（游戏全拉满 / 非游戏交回系统）",
                    "PIDFILE=/data/adb/xy_game.pid",
                    "echo \$\$ > \"\$PIDFILE\"",
                    "",
                    "N=\$(nproc 2>/dev/null || echo 8)",
                    "# 大核起始：按\"频率高于平均值的核\"（自适应 8/6/4 核）",
                    "BIG=\"\"",
                    "MAXF=0",
                    "for p in /sys/devices/system/cpu/cpufreq/policy*/; do",
                    "  [ -d \"\$p\" ] || continue",
                    "  f=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null)",
                    "  [ -n \"\$f\" ] || continue",
                    "  [ \"\$f\" -gt \"\$MAXF\" ] && MAXF=\$f",
                    "done",
                    "if [ \"\$MAXF\" -gt 0 ]; then",
                    "  for p in /sys/devices/system/cpu/cpufreq/policy*/; do",
                    "    [ -d \"\$p\" ] || continue",
                    "    f=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null)",
                    "    [ -n \"\$f\" ] || continue",
                    "    # 频率 > 最高 85% 的簇 = 大核簇",
                    "    [ \$((f * 100 / MAXF)) -ge 60 ] && {",
                    "      rc=\$(cat \"\$p/related_cpus\" 2>/dev/null | awk '{print \$1}')",
                    "      [ -n \"\$rc\" ] && { [ -z \"\$BIG\" ] || [ \"\$rc\" -lt \"\$BIG\" ]; } && BIG=\$rc",
                    "    }",
                    "  done",
                    "fi",
                    "[ -z \"\$BIG\" ] && BIG=\$((N / 2))",
                    "# 下限改 0（4 核全大核设备从 0 开始，不能强制 2）",
                    "[ \"\$BIG\" -lt 0 ] && BIG=0",
                    "[ \"\$BIG\" -ge \"\$N\" ] && BIG=\$((N - 1))",
                    "MASK=\$(printf '%x' \$(( (1 << N) - (1 << BIG) )))",
                    "",
                    "lock_val() {",
                    "  for p in \$2; do",
                    "    [ -f \"\$p\" ] || continue",
                    "    chmod 0666 \"\$p\" 2>/dev/null",
                    "    echo \"\$1\" > \"\$p\" 2>/dev/null",
                    "    chmod 0444 \"\$p\" 2>/dev/null",
                    "  done",
                    "}",
                    "",
                    "unlock_val() {",
                    "  for p in \$2; do",
                    "    [ -f \"\$p\" ] && chmod 0666 \"\$p\" 2>/dev/null",
                    "  done",
                    "}",
                    "",
                    "is_game() {",
                    "  case \"\$1\" in",
                    "    com.tencent.tmgp.sgame|com.tencent.tmgp.sgamece) return 0 ;;",
                    "    com.tencent.tmgp.pubgmhd|com.tencent.tmgp.pubgm) return 0 ;;",
                    "    com.tencent.ig|com.pubg.imobile|com.rekoo.pubgm) return 0 ;;",
                    "    com.tencent.tmgp.codev) return 0 ;;",
                    "    com.tencent.tmgp.dfm) return 0 ;;",
                    "    com.tencent.tmgp.cf) return 0 ;;",
                    "    com.tencent.mf.uam) return 0 ;;",
                    "    com.tencent.tmgp.cod) return 0 ;;",
                    "    com.tencent.KiHan|com.tencent.tmgp.hyrz) return 0 ;;",
                    "    com.tencent.tmgp.sk) return 0 ;;",
                    "    com.miHoYo.Yuanshen|com.miHoYo.GenshinImpact) return 0 ;;",
                    "    com.miHoYo.hkrpg) return 0 ;;",
                    "    com.kurogame.mingchao|com.kurogame.wutheringwaves.global) return 0 ;;",
                    "    com.tencent.lolm|com.tencent.jkchess|com.tencent.tmgp.wzry|com.tencent.tmgp.lol) return 0 ;;",
                    "    com.miHoYo.Nap|com.miHoYo.zzz) return 0 ;;",
                    "    com.netease.*|com.163.*|com.hpjy.*|com.lingfeng.*) return 0 ;;",
                    "    com.mojang.minecraftpe|com.epicgames.fortnite|com.activision.callofduty.*) return 0 ;;",
                    "    com.garena.game.*|com.riotgames.*|com.supercell.*|com.king.candycrush*) return 0 ;;",
                    "    com.mobile.legends|com.mobilelegends.*) return 0 ;;",
                    "    com.dts.freefireth|com.dts.freefiremax) return 0 ;;",
                    "    com.roblox.client) return 0 ;;",
                    "    com.papegames.*|com.hypergryph.*|com.sunborn.*) return 0 ;;",
                    "  esac",
                    "  return 1",
                    "}",
                    "",
                    "soc_temp() {",
                    "  # MTK",
                    "  for z in /sys/class/thermal/thermal_zone*/; do",
                    "    n=\$(cat \"\$z/type\" 2>/dev/null)",
                    "    case \"\$n\" in soc_max|soc_therm|soc-temp|bigcore_max|mtktscpu|soc_thermal|cpu-0-0-usr) t=\$(cat \"\$z/temp\" 2>/dev/null); [ -n \"\$t\" ] && echo \"\$t\" && return ;; esac",
                    "  done",
                    "  # 高通 / 通用：类型含 soc / cpu / gpu 的最高温",
                    "  best=0",
                    "  for z in /sys/class/thermal/thermal_zone*/; do",
                    "    n=\$(cat \"\$z/type\" 2>/dev/null)",
                    "    case \"\$n\" in",
                    "      *soc*|*Soc*|*SOC*|cpu-*|*gpu*|*GPU*|*tsens*)",
                    "        t=\$(cat \"\$z/temp\" 2>/dev/null)",
                    "        [ -z \"\$t\" ] && continue",
                    "        [ \"\$t\" -gt \"\$best\" ] 2>/dev/null && best=\$t",
                    "        ;;",
                    "    esac",
                    "  done",
                    "  echo \"\$best\"",
                    "}",
                    "",
                    "refresh_max() {",
                    "  F=\"\$1\"",
                    "  [ -n \"\$F\" ] || return",
                    "  settings put system peak_refresh_rate \"\$F\" 2>/dev/null",
                    "  settings put system min_refresh_rate \"\$F\" 2>/dev/null",
                    "  # 小米/澎湃/部分 ROM 用这些键",
                    "  settings put system user_refresh_rate \"\$F\" 2>/dev/null",
                    "  settings put secure user_refresh_rate \"\$F\" 2>/dev/null",
                    "  settings put secure peak_refresh_rate \"\$F\" 2>/dev/null",
                    "  setprop persist.sys.miui_refresh_rate \"\$F\" 2>/dev/null",
                    "  setprop persist.vendor.dfps.rate \"\$F\" 2>/dev/null",
                    "  # SurfaceFlinger 直接切到刷新率最高的 mode（settings 无效的 ROM 靠这个）",
                    "  MID=\$(/system/bin/dumpsys display 2>/dev/null | grep -oE \"DisplayMode\\{id=[0-9]+[^}]*peakRefreshRate=[0-9.]+\" | sed -E 's/.*id=([0-9]+).*peakRefreshRate=([0-9.]+).*/\\2 \\1/' | sort -rn | head -1 | awk '{print \$2}')",
                    "  [ -n \"\$MID\" ] && /system/bin/service call SurfaceFlinger 1035 i32 \"\$MID\" >/dev/null 2>&1",
                    "  [ -n \"\$MID\" ] && /system/bin/dumpsys SurfaceFlinger --set-active-mode \"\$MID\" >/dev/null 2>&1",
                    "}",
                    "",
                    "refresh_get() {",
                    "  for k in system/peak_refresh_rate system/min_refresh_rate system/user_refresh_rate secure/user_refresh_rate; do",
                    "    v=\$(settings get \"\${k%%/*}\" \"\${k##*/}\" 2>/dev/null | cut -d. -f1)",
                    "    [ -n \"\$v\" ] && [ \"\$v\" != \"null\" ] && { echo \"\$v\"; return; }",
                    "  done",
                    "  echo \"\"",
                    "}",
                    "",
                    "gpu_lock() {",
                    "  # ---- MTK 天玑：锁最高 OPP ----",
                    "  for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    chmod 0666 \"\$f\" 2>/dev/null",
                    "    echo -1 > \"\$f\" 2>/dev/null",
                    "    echo 0 0 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # ---- MTK 天玑 GED GPU 调度器 ----",
                    "  for f in /sys/kernel/ged/hal/dvfs_enable /sys/kernel/ged/hal/boost_gpu_enable; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    chmod 0666 \"\$f\" 2>/dev/null",
                    "    echo 1 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  for f in /sys/kernel/ged/hal/gpu_boost_level /sys/kernel/ged/hal/boost_gpu_level; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    chmod 0666 \"\$f\" 2>/dev/null",
                    "    echo 100 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # ---- 高通/Adreno：所有 kgsl 设备，governor + 最高档 ----",
                    "  for d in /sys/class/kgsl/kgsl-3d0 /sys/class/kgsl/kgsl-3d1 /sys/class/kgsl/kgsl-2d0; do",
                    "    [ -d \"\$d\" ] || continue",
                    "    [ -f \"\$d/devfreq/governor\" ] && { chmod 0666 \"\$d/devfreq/governor\" 2>/dev/null; echo performance > \"\$d/devfreq/governor\" 2>/dev/null; }",
                    "    [ -f \"\$d/pwrscale/trustzone/governor\" ] && echo performance > \"\$d/pwrscale/trustzone/governor\" 2>/dev/null",
                    "    [ -f \"\$d/min_pwrlevel\" ] && { chmod 0666 \"\$d/min_pwrlevel\" 2>/dev/null; echo 0 > \"\$d/min_pwrlevel\" 2>/dev/null; }",
                    "    [ -f \"\$d/max_pwrlevel\" ] && { chmod 0666 \"\$d/max_pwrlevel\" 2>/dev/null; echo 0 > \"\$d/max_pwrlevel\" 2>/dev/null; }",
                    "    [ -f \"\$d/max_gpuclk\" ] && { chmod 0666 \"\$d/max_gpuclk\" 2>/dev/null; echo 0 > \"\$d/max_gpuclk\" 2>/dev/null; }",
                    "    for f in \"\$d/devfreq/min_freq\" \"\$d/devfreq/max_freq\"; do",
                    "      [ -f \"\$f\" ] || continue",
                    "      mx=\$(cat \"\$d/devfreq/max_freq\" 2>/dev/null)",
                    "      [ -n \"\$mx\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo \"\$mx\" > \"\$f\" 2>/dev/null",
                    "    done",
                    "  done",
                    "  # ---- 高通 msm_performance：每簇频率下限（骁龙专用）----",
                    "  if [ -f /sys/module/msm_performance/parameters/cpu_min_freq ]; then",
                    "    VALS=\"\"",
                    "    for p in /sys/devices/system/cpu/cpufreq/policy*/; do",
                    "      [ -d \"\$p\" ] || continue",
                    "      n=\$(basename \"\$p\" | sed 's/policy//')",
                    "      mf=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null)",
                    "      [ -n \"\$mf\" ] && VALS=\"\$VALS \$n:\$mf\"",
                    "    done",
                    "    [ -n \"\$VALS\" ] && { chmod 0666 /sys/module/msm_performance/parameters/cpu_min_freq 2>/dev/null; echo \"\$VALS\" > /sys/module/msm_performance/parameters/cpu_min_freq 2>/dev/null; }",
                    "  fi",
                    "  if [ -f /sys/module/msm_performance/parameters/cpu_max_freq ]; then",
                    "    [ -n \"\$VALS\" ] && echo \"\$VALS\" > /sys/module/msm_performance/parameters/cpu_max_freq 2>/dev/null",
                    "  fi",
                    "  # 骁龙 uclamp：拉满 top-app",
                    "  for f in /dev/cpuctl/top-app/cpu.uclamp.min /sys/fs/cgroup/top-app/uclamp.min /proc/sys/kernel/sched_util_clamp_min; do",
                    "    [ -f \"\$f\" ] && { chmod 0644 \"\$f\" 2>/dev/null; echo 1024 > \"\$f\" 2>/dev/null; }",
                    "  done",
                    "  # ---- 高通 kgsl devfreq：min=max（真正锁频）----",
                    "  for d in /sys/class/kgsl/kgsl-3d0/devfreq /sys/class/kgsl/kgsl-3d1/devfreq; do",
                    "    [ -d \"\$d\" ] || continue",
                    "    mx=\$(cat \"\$d/max_freq\" 2>/dev/null); [ -n \"\$mx\" ] || continue",
                    "    chmod 0666 \"\$d/min_freq\" \"\$d/max_freq\" \"\$d/governor\" 2>/dev/null",
                    "    echo \"\$mx\" > \"\$d/max_freq\" 2>/dev/null",
                    "    echo \"\$mx\" > \"\$d/min_freq\" 2>/dev/null",
                    "    echo performance > \"\$d/governor\" 2>/dev/null",
                    "  done",
                    "  # ---- 内存带宽 DDR/busmon ----",
                    "  for d in /sys/class/devfreq/*/; do",
                    "    [ -d \"\$d\" ] || continue",
                    "    b=\$(basename \"\$d\")",
                    "    case \"\$b\" in *ddr*|*DDR*|*busmon*|*dvfsrc*|*bw*|*BW*|*memlat*|*cpubw*) ;; *) continue ;; esac",
                    "    case \"\$b\" in *gpu*|*mali*|*kgsl-3d*) continue ;; esac",
                    "    mx=\$(cat \"\$d/max_freq\" 2>/dev/null); [ -n \"\$mx\" ] && [ \"\$mx\" != \"0\" ] || continue",
                    "    chmod 0666 \"\$d/min_freq\" 2>/dev/null",
                    "    echo \"\$mx\" > \"\$d/min_freq\" 2>/dev/null",
                    "  done",
                    "  # ---- 通用 devfreq：名字里带 gpu/mali/kgsl/adreno 的都锁最高 ----",
                    "  for d in /sys/class/devfreq/*; do",
                    "    [ -d \"\$d\" ] || continue",
                    "    b=\$(basename \"\$d\")",
                    "    case \"\$b\" in",
                    "      *gpu*|*GPU*|*mali*|*Mali*|*kgsl*|*adreno*|*Adreno*|*gpufreq*) ;;",
                    "      *) continue ;;",
                    "    esac",
                    "    mx=\$(cat \"\$d/max_freq\" 2>/dev/null)",
                    "    [ -n \"\$mx\" ] && [ \"\$mx\" != \"0\" ] || continue",
                    "    for f in \"\$d/min_freq\" \"\$d/max_freq\"; do",
                    "      [ -f \"\$f\" ] || continue",
                    "      chmod 0666 \"\$f\" 2>/dev/null",
                    "      echo \"\$mx\" > \"\$f\" 2>/dev/null",
                    "    done",
                    "    [ -f \"\$d/governor\" ] && { chmod 0666 \"\$d/governor\" 2>/dev/null; echo performance > \"\$d/governor\" 2>/dev/null; }",
                    "  done",
                    "}",
                    "",
                    "gpu_unlock() {",
                    "  if [ -f /proc/gpufreqv2/fix_target_opp_index ]; then",
                    "    chmod 0666 /proc/gpufreqv2/fix_target_opp_index 2>/dev/null",
                    "    echo -1 > /proc/gpufreqv2/fix_target_opp_index 2>/dev/null",
                    "  fi",
                    "  if [ -f /sys/class/kgsl/kgsl-3d0/devfreq/governor ]; then",
                    "    chmod 0666 /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null",
                    "    echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null",
                    "  fi",
                    "  for f in /sys/class/devfreq/*gpu*/min_freq; do",
                    "    [ -f \"\$f\" ] && chmod 0666 \"\$f\" 2>/dev/null",
                    "  done",
                    "}",
                    "",
                    "apply_max() {",
                    "  # ① 先把 scaling_max 提到硬件上限（否则下面的 min 85% 会被拒）",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_max_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    b=\${f%/scaling_max_freq}",
                    "    hw=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "    [ -n \"\$hw\" ] || continue",
                    "    cur=\$(cat \"\$f\" 2>/dev/null)",
                    "    [ \"\$cur\" != \"\$hw\" ] && { chmod 0666 \"\$f\" 2>/dev/null; echo \"\$hw\" > \"\$f\" 2>/dev/null; }",
                    "  done",
                    "  # Scene EP 极速：下限 40%（不钉死，让 sugov 自己爬到满）+ target_loads 30 + 立即升频",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    b=\${f%/scaling_min_freq}",
                    "    mf=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "    [ -n \"\$mf\" ] || continue",
                    "    want=\$mf",
                    "    # 写入后回读校验，最多重试 2 次（不阻塞，守护会持续维护）",
                    "    a=0",
                    "    while [ \"\$a\" -lt 2 ]; do",
                    "      cur=\$(cat \"\$f\" 2>/dev/null)",
                    "      [ \"\$cur\" = \"\$want\" ] && break",
                    "      chmod 0666 \"\$f\" 2>/dev/null",
                    "      echo \"\$want\" > \"\$f\" 2>/dev/null",
                    "      a=\$((a + 1))",
                    "      sleep 0.2",
                    "    done",
                    "  done",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/target_loads; do",
                    "    [ -f \"\$f\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo 20 > \"\$f\" 2>/dev/null && chmod 0444 \"\$f\" 2>/dev/null",
                    "  done",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/up_rate_limit_us; do",
                    "    [ -f \"\$f\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo 0 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/down_rate_limit_us; do",
                    "    [ -f \"\$f\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo 5000 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # 全核 online",
                    "  for c in /sys/devices/system/cpu/cpu[0-9]*/online; do",
                    "    [ -f \"\$c\" ] && echo 1 > \"\$c\" 2>/dev/null",
                    "  done",
                    "  # 关闭 CPU 深度空闲（只留最浅），触摸唤醒更快",
                    "  for st in /sys/devices/system/cpu/cpuidle/state*/; do",
                    "    [ -d \"\$st\" ] || continue",
                    "    nm=\$(cat \"\$st/name\" 2>/dev/null)",
                    "    idx=\$(basename \"\$st\" | sed 's/state//')",
                    "    # 保留 state0（WFI），禁掉更深的",
                    "    if [ \"\${idx:-0}\" -gt 0 ] 2>/dev/null; then",
                    "      [ -f \"\$st/disable\" ] && echo 1 > \"\$st/disable\" 2>/dev/null",
                    "    fi",
                    "  done",
                    "  # GPU 下限锁到最高（不只锁 OPP 表）",
                    "  for f in /sys/class/devfreq/*mali*/min_freq /sys/class/devfreq/*gpu*/min_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    mx=\$(cat \"\${f%/min_freq}/max_freq\" 2>/dev/null)",
                    "    [ -n \"\$mx\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo \"\$mx\" > \"\$f\" 2>/dev/null && chmod 0444 \"\$f\" 2>/dev/null",
                    "  done",
                    "  # ---------- 压制厂商调频/温控服务（它们会把参数改回去）----------",
                    "  # ---------- 温控放宽（游戏时，有散热器；退出时还原）----------",
                    "  for z in /sys/class/thermal/thermal_zone*/; do",
                    "    [ -d \"\$z\" ] || continue",
                    "    for tp in \"\$z\"trip_point_*_temp; do",
                    "      [ -f \"\$tp\" ] || continue",
                    "      cur=\$(cat \"\$tp\" 2>/dev/null)",
                    "      [ -z \"\$cur\" ] && continue",
                    "      [ \"\$cur\" -lt 90000 ] 2>/dev/null && {",
                    "        echo \"\$tp:\$cur\" >> /data/adb/xy_thermal_bak 2>/dev/null",
                    "        chmod 0666 \"\$tp\" 2>/dev/null",
                    "        echo 95000 > \"\$tp\" 2>/dev/null",
                    "      }",
                    "    done",
                    "  done",
                    "  # 调度/渲染层优化（防系统拖帧）",
                    "  # 1) 调度 boost：让 schedtune/sched_boost 偏性能",
                    "  for f in /proc/sys/kernel/sched_boost /dev/stune/top-app/schedtune.boost /dev/stune/schedtune.boost; do",
                    "    [ -f \"\$f\" ] && { chmod 0644 \"\$f\" 2>/dev/null; echo 1 > \"\$f\" 2>/dev/null; }",
                    "  done",
                    "  for f in /dev/stune/top-app/schedtune.prefer_idle /dev/stune/schedtune.prefer_idle; do",
                    "    [ -f \"\$f\" ] && echo 0 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # 2) HWUI 强制 GPU 渲染（避免软件渲染拖帧）",
                    "  setprop debug.hwui.renderer skiavk 2>/dev/null",
                    "  setprop debug.hwui.render_ahead 1 2>/dev/null",
                    "  # 3) LMK 放宽（避免后台被杀导致游戏重载卡顿）",
                    "  # LMK 不动（放宽会导致内存爆/卡顿）",
                    "  # 内存回收压力放宽（减少 GC/回收导致掉帧）",
                    "  for f in /proc/sys/vm/swappiness /proc/sys/vm/vfs_cache_pressure; do",
                    "    [ -f \"\$f\" ] && echo 60 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # 注意：不禁用 zram（禁用会导致内存不足→卡顿）",
                    "  # 4) 关掉 tracing/debug 开销",
                    "  echo 0 > /sys/kernel/debug/tracing/tracing_on 2>/dev/null",
                    "  echo 0 > /sys/kernel/debug/tracing/events/enable 2>/dev/null",
                    "  # 5) 关掉不必要的内核调试（若有）",
                    "  echo -1 > /proc/sys/kernel/perf_event_paranoid 2>/dev/null",
                    "  # ===== 按厂商细分适配（小米/红米、一加/OPPO/真我、联想、vivo/iQOO）=====",
                    "  BRAND=\$(getprop ro.product.brand 2>/dev/null | tr 'A-Z' 'a-z')",
                    "  MANUF=\$(getprop ro.product.manufacturer 2>/dev/null | tr 'A-Z' 'a-z')",
                    "  case \"\$BRAND\$MANUF\" in",
                    "    *xiaomi*|*redmi*|*poco*)",
                    "      # 小米/红米/POCO：MIUI/澎湃",
                    "      settings put system user_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      settings put secure miui_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      setprop persist.sys.miui_optimization 1 2>/dev/null",
                    "      setprop persist.sys.powmillet.enable 0 2>/dev/null",
                    "      setprop persist.sys.miui_game_turbo 1 2>/dev/null",
                    "      for pkg in com.miui.gamebooster com.miui.gameturbo com.miui.powerkeeper com.xiaomi.gamecenter com.miui.newhome com.miui.gamebox com.miui.gamebar com.xiaomi.migameservice; do",
                    "        am force-stop \"\$pkg\" >/dev/null 2>&1",
                    "      done",
                    "      # 小米的电池温控",
                    "      for z in /sys/class/thermal/thermal_zone*/; do",
                    "        n=\$(cat \"\$z/type\" 2>/dev/null)",
                    "        case \"\$n\" in *batt*|*charger*) for tp in \"\$z\"trip_point_*_temp; do [ -f \"\$tp\" ] && echo 60000 > \"\$tp\" 2>/dev/null; done ;; esac",
                    "      done",
                    "      ;;",
                    "    *oppo*|*oneplus*|*realme*|*oplus*)",
                    "      # OPPO/一加/真我：ColorOS/realmeUI",
                    "      settings put system peak_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      settings put system user_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      setprop persist.vendor.dfps.rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      setprop persist.sys.oplus.performance 1 2>/dev/null",
                    "      setprop persist.sys.oplus.games.performance 1 2>/dev/null",
                    "      for pkg in com.coloros.gamespace com.oplus.games com.oplus.gamespace com.oneplus.gamespace com.realme.gamespace com.realme.gameassistant com.heytap.gameassistant com.oppo.gamebox com.oplus.battery com.coloros.oppoguardelf com.coloros.gamespaceui com.oplus.gamespaceui com.oplus.gamebox com.coloros.gamebar; do",
                    "        am force-stop \"\$pkg\" >/dev/null 2>&1",
                    "      done",
                    "      ;;",
                    "    *lenovo*|*motorola*|*moto*|*zui*)",
                    "      # 联想/摩托",
                    "      settings put system peak_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      setprop persist.sys.lenovo.performance 1 2>/dev/null",
                    "      for pkg in com.lenovo.gameassistant com.zui.gameassistant com.motorola.gameassistant com.motorola.gamemode com.zui.gamespace com.lenovo.gamespace com.lenovo.gamebar; do",
                    "        am force-stop \"\$pkg\" >/dev/null 2>&1",
                    "      done",
                    "      ;;",
                    "    *vivo*|*iqoo*)",
                    "      # vivo/iQOO：OriginOS",
                    "      settings put system peak_refresh_rate \"\$(cat /data/adb/xy_maxf 2>/dev/null)\" 2>/dev/null",
                    "      settings put global game_mode_performance 1 2>/dev/null",
                    "      setprop persist.sys.vivo.performance 1 2>/dev/null",
                    "      setprop persist.vivo.thermal.mode 0 2>/dev/null",
                    "      for pkg in com.vivo.game com.vivo.gamewatch com.iqoo.gameassistant com.vivo.battery; do",
                    "        am force-stop \"\$pkg\" >/dev/null 2>&1",
                    "      done",
                    "      ;;",
                    "  esac",
                    "  # 压制系统游戏助手/游戏空间（它们会限帧、降画质）",
                    "  # 全厂商游戏助手/性能/安全包（精确列表，覆盖小米红米/一加OPPO真我/联想摩托/vivo iQOO/黑鲨红魔努比亚）",
                    "  for pkg in \$ALL_PKGS; do",
                    "    pm list packages 2>/dev/null | grep -q \"package:\$pkg\" || continue",
                    "    am force-stop \"\$pkg\" >/dev/null 2>&1",
                    "    pm disable-user --user 0 \"\$pkg\" >/dev/null 2>&1 && echo \"\$pkg\" >> /data/adb/xy_game_pkgs_disabled 2>/dev/null",
                    "  done",
                    "  # thermal zone 策略禁用（不停服务，只关降温策略）",
                    "  for z in /sys/class/thermal/thermal_zone*/; do",
                    "    [ -f \"\$z/mode\" ] || continue",
                    "    chmod 0644 \"\$z/mode\" 2>/dev/null",
                    "    echo disabled > \"\$z/mode\" 2>/dev/null",
                    "    echo \"\$z\" >> /data/adb/xy_thermal_zones 2>/dev/null",
                    "  done",
                    "  # 电池温控放宽（电池高温也会触发降频/限流）",
                    "  for z in /sys/class/thermal/thermal_zone*/; do",
                    "    [ -d \"\$z\" ] || continue",
                    "    n=\$(cat \"\$z/type\" 2>/dev/null)",
                    "    case \"\$n\" in *battery*|*bms*|*bcl*|*vbat*|*ibat*|*chg*) ;; *) continue ;; esac",
                    "    for tp in \"\$z\"trip_point_*_temp; do",
                    "      [ -f \"\$tp\" ] || continue",
                    "      cur=\$(cat \"\$tp\" 2>/dev/null)",
                    "      [ -z \"\$cur\" ] && continue",
                    "      [ \"\$cur\" -lt 60000 ] 2>/dev/null && {",
                    "        chmod 0666 \"\$tp\" 2>/dev/null",
                    "        echo 60000 > \"\$tp\" 2>/dev/null",
                    "      }",
                    "    done",
                    "  done",
                    "  # 系统游戏模式/游戏空间限制",
                    "  settings put global game_mode_performance 1 2>/dev/null",
                    "  settings put global game_mode_battery 0 2>/dev/null",
                    "  settings put global miui_game_turbo 1 2>/dev/null",
                    "  settings put global game_acceleration 1 2>/dev/null",
                    "  settings put global game_mode_enabled 1 2>/dev/null",
                    "  settings put global low_power_mode 0 2>/dev/null",
                    "  # 各 ROM 性能开关（有就设）",
                    "  setprop persist.sys.oplus.performance 1 2>/dev/null",
                    "  setprop persist.sys.miui.performance 1 2>/dev/null",
                    "  setprop persist.vendor.qti.games.gt.prof 1 2>/dev/null",
                    "  setprop persist.sys.game_mode 1 2>/dev/null",
                    "  setprop debug.game.gpu.enforce 1 2>/dev/null",
                    "  # MTK cm_mgr：解除带宽封顶",
                    "  [ -f /sys/kernel/cm_mgr/dbg_cm_mgr ] && {",
                    "    echo \"cm_mgr_perf_enable 1\" > /sys/kernel/cm_mgr/dbg_cm_mgr 2>/dev/null",
                    "    echo \"cm_mgr_dram_opp_ceiling -1\" > /sys/kernel/cm_mgr/dbg_cm_mgr 2>/dev/null",
                    "  }",
                    "  # 后台进程绑小核（让大核全给游戏）",
                    "  MASK_SMALL=\$(printf '%x' \$(( (1 << BIG) - 1 )))",
                    "  for cg in /dev/cpuset/background /dev/cpuset/system-background /dev/cpuset/restricted; do",
                    "    [ -f \"\$cg/cgroup.procs\" ] || continue",
                    "    for pid in \$(cat \"\$cg/cgroup.procs\" 2>/dev/null); do",
                    "      [ -n \"\$pid\" ] && taskset -p \"\$MASK_SMALL\" \"\$pid\" >/dev/null 2>&1",
                    "    done",
                    "  done",
                    "  # 释放内存（游戏前清后台 + 缓存）",
                    "  am kill-all >/dev/null 2>&1",
                    "  sync",
                    "  echo 1 > /proc/sys/vm/drop_caches 2>/dev/null",
                    "  echo 2 > /proc/sys/vm/drop_caches 2>/dev/null",
                    "  gpu_lock",
                    "  # 刷新率：自适应设备最高",
                    "  MAXF=\$(/system/bin/dumpsys display 2>/dev/null | grep -oE \"fps=[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/dumpsys SurfaceFlinger 2>/dev/null | grep -oE \"refresh-rate *[:=] *[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get secure user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "  [ \"\$MAXF\" = \"null\" ] && MAXF=\"\"",
                    "  if [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null; then",
                    "    echo \"\$MAXF\" > /data/adb/xy_maxf 2>/dev/null",
                    "    refresh_max \"\$MAXF\"",
                    "  fi",
                    "}",
                    "",
                    "restore_default() {",
                    "  # 恢复游戏助手/安全中心（文件丢了也按完整列表恢复）",
                    "  if [ -f /data/adb/xy_game_pkgs_disabled ]; then",
                    "    while read -r pkg; do",
                    "      [ -n \"\$pkg\" ] && pm enable --user 0 \"\$pkg\" >/dev/null 2>&1",
                    "    done < /data/adb/xy_game_pkgs_disabled",
                    "    rm -f /data/adb/xy_game_pkgs_disabled",
                    "  else",
                    "    for pkg in \\",
                    "      com.oplus.games com.coloros.gamespace com.oplus.gamespace com.oneplus.gamespace com.realme.gamespace \\",
                    "      com.miui.gamebooster com.miui.gameturbo com.miui.newhome com.miui.gamebox com.miui.gamebar \\",
                    "      com.miui.securitycenter com.miui.powerkeeper com.miui.analytics com.miui.cleanmaster \\",
                    "      com.coloros.secureguard com.oplus.battery com.oplus.athena com.coloros.oppoguardelf com.oplus.safecenter \\",
                    "      com.vivo.game com.vivo.gamewatch com.iqoo.gameassistant com.vivo.abe com.vivo.pem com.vivo.battery \\",
                    "      com.lenovo.gameassistant com.zui.gameassistant com.zui.gamespace com.lenovo.gamespace \\",
                    "      com.motorola.gameassistant com.motorola.gamemode com.zui.safecenter com.lenovo.safecenter; do",
                    "      pm enable --user 0 \"\$pkg\" >/dev/null 2>&1",
                    "    done",
                    "  fi",
                    "  # 还原温控",
                    "  if [ -f /data/adb/xy_thermal_bak ]; then",
                    "    while IFS=: read -r tp val; do",
                    "      [ -f \"\$tp\" ] && [ -n \"\$val\" ] && chmod 0666 \"\$tp\" 2>/dev/null && echo \"\$val\" > \"\$tp\" 2>/dev/null",
                    "    done < /data/adb/xy_thermal_bak",
                    "    rm -f /data/adb/xy_thermal_bak",
                    "  fi",
                    "  # 还原 DDR 下限（写回可用最低频）",
                    "  for df in /sys/class/devfreq/mtk-dvfsrc-devfreq /sys/class/devfreq/*ddr* /sys/class/devfreq/*bw*; do",
                    "    [ -d \"\$df\" ] || continue",
                    "    mn=\$(cat \"\$df/available_frequencies\" 2>/dev/null | awk '{print \$1}')",
                    "    [ -z \"\$mn\" ] && mn=\$(cat \"\$df/min_freq\" 2>/dev/null)",
                    "    [ -n \"\$mn\" ] && { chmod 0666 \"\$df/min_freq\" 2>/dev/null; echo \"\$mn\" > \"\$df/min_freq\" 2>/dev/null; }",
                    "  done",
                    "  # 还原 I/O 调度器",
                    "  for q in /sys/block/sd*/queue /sys/block/mmcblk*/queue /sys/block/dm-*/queue; do",
                    "    [ -d \"\$q\" ] || continue",
                    "    echo mq-deadline > \"\$q/scheduler\" 2>/dev/null || echo bfq > \"\$q/scheduler\" 2>/dev/null",
                    "  done",
                    "  # 还原 GED",
                    "  for f in /sys/kernel/ged/hal/gpu_boost_level /sys/kernel/ged/hal/boost_gpu_level; do",
                    "    [ -f \"\$f\" ] && echo 0 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  # 还原骁龙参数",
                    "  if [ -f /sys/module/msm_performance/parameters/cpu_min_freq ]; then",
                    "    : > /sys/module/msm_performance/parameters/cpu_min_freq 2>/dev/null",
                    "  fi",
                    "  # 还原 CPU 深度空闲",
                    "  for st in /sys/devices/system/cpu/cpuidle/state*/disable; do",
                    "    [ -f \"\$st\" ] && echo 0 > \"\$st\" 2>/dev/null",
                    "  done",
                    "  # 还原 GPU 下限",
                    "  for f in /sys/class/devfreq/*mali*/min_freq /sys/class/devfreq/*gpu*/min_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    mn=\$(cat \"\${f%/min_freq}/available_frequencies\" 2>/dev/null | awk '{print \$1}')",
                    "    [ -n \"\$mn\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo \"\$mn\" > \"\$f\" 2>/dev/null",
                    "  done",
                    "  gpu_unlock",
                    "  rm -f /data/adb/xy_maxf 2>/dev/null",
                    "  settings delete system peak_refresh_rate 2>/dev/null",
                    "  settings delete system min_refresh_rate 2>/dev/null",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    b=\${f%/scaling_min_freq}",
                    "    mn=\$(cat \"\$b/cpuinfo_min_freq\" 2>/dev/null)",
                    "    [ -n \"\$mn\" ] || continue",
                    "    chmod 0666 \"\$f\" 2>/dev/null",
                    "    echo \"\$mn\" > \"\$f\" 2>/dev/null",
                    "  done",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/target_loads; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    chmod 0666 \"\$f\" 2>/dev/null",
                    "    echo 95 > \"\$f\" 2>/dev/null",
                    "  done",
                    "  unlock_val /proc/sys/kernel/sched_util_clamp_min",
                    "  for d in /dev/cpuset/top-app /sys/fs/cgroup/top-app; do",
                    "    [ -d \"\$d\" ] || continue",
                    "    unlock_val \"\$d/uclamp.min\"",
                    "    [ -f \"\$d/uclamp.min\" ] && echo 0 > \"\$d/uclamp.min\" 2>/dev/null",
                    "  done",
                    "  for f in /sys/module/mtk_fpsgo/parameters/p_perf; do",
                    "    [ -f \"\$f\" ] && chmod 0666 \"\$f\" 2>/dev/null && echo 1 > \"\$f\" 2>/dev/null",
                    "  done",
                    "}",
                    "",
                    "boost_threads() {",
                    "  pid=\$1",
                    "  [ -n \"\$pid\" ] || return",
                    "  # 强制移到 top-app（游戏常被系统丢进 background cgroup）",
                    "  for d in /dev/cpuset/top-app /sys/fs/cgroup/top-app /dev/cpuset/foreground; do",
                    "    [ -f \"\$d/cgroup.procs\" ] && echo \"\$pid\" > \"\$d/cgroup.procs\" 2>/dev/null",
                    "  done",
                    "  # 全核 affinity（先放宽到全核，再绑大核）",
                    "  taskset -p \"\$(printf '%x' \$(( (1 << N) - 1 )))\" \"\$pid\" >/dev/null 2>&1",
                    "  # top-app CPU 权重拉满（游戏优先拿 CPU）",
                    "  for f in /dev/cpuctl/top-app/cpu.shares /sys/fs/cgroup/top-app/cpu.weight; do",
                    "    [ -f \"\$f\" ] && { chmod 0644 \"\$f\" 2>/dev/null; echo 2048 > \"\$f\" 2>/dev/null; }",
                    "  done",
                    "  for t in /proc/\$pid/task/*; do",
                    "    [ -d \"\$t\" ] || continue",
                    "    tid=\${t##*/}",
                    "    nm=\$(cat \"\$t/comm\" 2>/dev/null)",
                    "    case \"\$nm\" in",
                    "      RenderThread|hwuiTask*|*GLThread*|UnityMain|*Render*|*render*)",
                    "        renice -n -10 -p \"\$tid\" >/dev/null 2>&1",
                    "        taskset -p \"\$MASK\" \"\$tid\" >/dev/null 2>&1",
                    "        ;;",
                    "      *)",
                    "        [ \"\$tid\" = \"\$pid\" ] && taskset -p \"\$MASK\" \"\$tid\" >/dev/null 2>&1",
                    "        ;;",
                    "    esac",
                    "  done",
                    "}",
                    "",
                    "state=normal",
                    "pkg=\"\"",
                    "ALL_PKGS=\"com.miui.gamebooster com.miui.gameturbo com.miui.newhome com.miui.gamebox com.miui.gamebar com.xiaomi.gamecenter com.xiaomi.gamecenter.sdk com.xiaomi.migameservice com.miui.securitycenter com.miui.powerkeeper com.miui.securityadd com.miui.analytics com.miui.cleanmaster com.miui.battery com.miui.castallshare com.xiaomi.miplay_client com.xiaomi.mirror com.miui.voiceassist com.xiaomi.aiasst.service com.miui.notes com.coloros.gamespace com.oplus.games com.oplus.gamespace com.oneplus.gamespace com.realme.gamespace com.realme.gameassistant com.heytap.gameassistant com.oppo.gamebox com.coloros.gamespaceui com.oplus.gamespaceui com.oplus.gamebox com.coloros.gamebar com.coloros.secureguard com.oplus.battery com.oplus.athena com.coloros.oppoguardelf com.oplus.safecenter com.coloros.safecenter com.coloros.healthcheck com.oplus.healthcheck com.lenovo.gameassistant com.zui.gameassistant com.zui.gamespace com.lenovo.gamespace com.lenovo.gamebar com.motorola.gameassistant com.motorola.gamemode com.zui.safecenter com.lenovo.safecenter com.motorola.safecenter com.vivo.game com.vivo.gamewatch com.iqoo.gameassistant com.vivo.abe com.vivo.pem com.vivo.battery com.iqoo.secure com.blackshark.gameassistant com.redmagic.gameassistant com.blackshark.safecenter com.nubia.safecenter com.nubia.gameassistant com.oplus.screenrecorder com.coloros.screenrecorder com.oplus.cast com.coloros.cast com.oplus.voiceassist com.coloros.breeno com.heytap.breeno com.oplus.note com.coloros.note com.vivo.smartmirror com.vivo.voiceassist com.vivo.jovi com.vivo.notes com.vivo.gamecube com.xiaomi.joyose com.miui.joyose com.motorola.screenrecorder com.motorola.voice com.motorola.notes com.zui.notes com.miui.cloudservice com.miui.cloudbackup com.miui.gallery com.miui.msa com.xiaomi.mipicks com.miui.personalassistant com.android.printspooler com.xiaomi.smarthome com.miui.weather2 com.coloros.cloud com.oplus.cloud com.coloros.gallery3d com.oplus.ads com.coloros.assistantscreen com.vivo.easyshare com.vivo.gallery com.vivo.ads com.vivo.assistant com.vivo.weather2 com.mediatek.mtklogger com.mediatek.batterywarning com.mediatek.dm com.mediatek.ims com.mediatek.mtklogger.framework com.mediatek.timeshift.compute com.qualcomm.qti.qms.service.telemetry com.qualcomm.qti.qcolor com.qualcomm.qti.perfd vendor.qti.perfd1.0 com.qualcomm.qti.qms.service.performance\"",
                    "# 启动时先恢复一次（清理上次异常退出的残留禁用）",
                    "restore_default",
                    "# 非游戏状态兜底恢复的包列表",
                    "XY_PKGS=\"\$ALL_PKGS\" # com.xiaomi.joyose com.miui.joyose com.oplus.games com.coloros.gamespace com.oplus.gamespace com.oneplus.gamespace com.realme.gamespace com.miui.gamebooster com.miui.gameturbo com.miui.newhome com.miui.gamebox com.miui.gamebar com.miui.securitycenter com.miui.powerkeeper com.miui.analytics com.miui.cleanmaster com.coloros.secureguard com.oplus.battery com.oplus.athena com.coloros.oppoguardelf com.oplus.safecenter com.vivo.game com.vivo.gamewatch com.iqoo.gameassistant com.vivo.abe com.vivo.pem com.vivo.battery com.lenovo.gameassistant com.zui.gameassistant com.zui.gamespace com.lenovo.gamespace com.motorola.gameassistant com.motorola.gamemode com.zui.safecenter com.lenovo.safecenter com.miui.castallshare com.xiaomi.miplay_client com.xiaomi.mirror com.miui.voiceassist com.miui.notes com.oplus.screenrecorder com.oplus.cast com.coloros.breeno com.coloros.note com.vivo.smartmirror com.vivo.jovi com.vivo.notes\"",
                    "",
                    "tick=0",
                    "while true; do",
                    "  tick=\$((tick + 1))",
                    "  # dumpsys 较重：每 10 秒（5 轮）才查一次前台，其余轮次只做轻量维护",
                    "  if [ \$((tick % 10)) = 1 ]; then",
                    "    newpkg=\$(dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity | sed -E 's/.* ([a-zA-Z0-9_.]+)\\/.*/\\1/')",
                    "    [ -n \"\$newpkg\" ] && pkg=\$newpkg",
                    "    echo \"\$pkg\" > /data/adb/xy_cur_app 2>/dev/null",
                    "  fi",
                    "",
                    "  # 温度 10 秒读一次（省 CPU）",
                    "  if [ \$((tick % 10)) = 1 ]; then",
                    "    temp=\$(soc_temp)",
                    "  fi",
                    "  [ -z \"\$temp\" ] && temp=0",
                    "  if is_game \"\$pkg\" && [ \"\$temp\" -lt 92000 ] 2>/dev/null; then",
                    "    if [ \"\$state\" != \"game\" ]; then",
                    "      apply_max",
                    "      lock_val 1024 /proc/sys/kernel/sched_util_clamp_min",
                    "      for d in /dev/cpuset/top-app /sys/fs/cgroup/top-app; do",
                    "        [ -d \"\$d\" ] || continue",
                    "        lock_val 1024 \"\$d/uclamp.min\"",
                    "      done",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/target_loads; do",
                    "        lock_val 20 \"\$f\"",
                    "      done",
                    "      # DDR 带宽守护（min=max，被改回就重设）",
                    "      for d in /sys/class/devfreq/*/; do",
                    "        [ -d \"\$d\" ] || continue",
                    "        b=\$(basename \"\$d\")",
                    "        case \"\$b\" in *ddr*|*DDR*|*busmon*|*dvfsrc*|*bw*|*BW*|*memlat*|*cpubw*) ;; *) continue ;; esac",
                    "        case \"\$b\" in *gpu*|*mali*|*kgsl-3d*) continue ;; esac",
                    "        mx=\$(cat \"\$d/max_freq\" 2>/dev/null); [ -n \"\$mx\" ] && [ \"\$mx\" != \"0\" ] || continue",
                    "        cur=\$(cat \"\$d/min_freq\" 2>/dev/null)",
                    "        [ \"\$cur\" = \"\$mx\" ] && continue",
                    "        chmod 0666 \"\$d/min_freq\" 2>/dev/null",
                    "        echo \"\$mx\" > \"\$d/min_freq\" 2>/dev/null",
                    "      done",
                    "      # sched_boost 守护",
                    "      for f in /proc/sys/kernel/sched_boost /dev/stune/top-app/schedtune.boost; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        [ \"\$(cat \"\$f\" 2>/dev/null)\" = \"1\" ] && continue",
                    "        chmod 0644 \"\$f\" 2>/dev/null",
                    "        echo 1 > \"\$f\" 2>/dev/null",
                    "      done",
                    "      # 实际显示模式守护（防系统在 SurfaceFlinger 层切到低刷新率）",
                    "      if [ \$((tick % 10)) = 1 ]; then",
                    "        MAXF=\$(cat /data/adb/xy_maxf 2>/dev/null)",
                    "        if [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null; then",
                    "          ACT=\$(/system/bin/dumpsys display 2>/dev/null | grep -m1 'mActiveSfDisplayMode' | grep -oE 'peakRefreshRate=[0-9.]+' | grep -oE '[0-9]+')",
                    "          if [ -n \"\$ACT\" ] && [ \"\$ACT\" -lt \"\$MAXF\" ] 2>/dev/null; then",
                    "            refresh_max \"\$MAXF\"",
                    "          fi",
                    "        fi",
                    "      fi",
                    "      # thermal zone 策略守护（每轮确保 disabled）",
                    "      for z in /sys/class/thermal/thermal_zone*/mode; do",
                    "        [ -f \"\$z\" ] || continue",
                    "        [ \"\$(cat \"\$z\" 2>/dev/null)\" = \"disabled\" ] && continue",
                    "        chmod 0644 \"\$z\" 2>/dev/null",
                    "        echo disabled > \"\$z\" 2>/dev/null",
                    "      done",
                    "      # 防系统锁 60：禁用 LTPO/智能刷新率/游戏模式限帧/省电",
                    "  setprop debug.sf.enable_ltpo 0 2>/dev/null",
                    "  setprop debug.sf.disable_backpressure 1 2>/dev/null",
                    "  setprop debug.sf.latch_unsignaled 1 2>/dev/null",
                    "  settings put global game_mode_refresh_rate 0 2>/dev/null",
                    "  settings put system smart_refresh_rate 0 2>/dev/null",
                    "  settings put system intelligent_refresh_rate 0 2>/dev/null",
                    "  settings put global low_power 0 2>/dev/null",
                    "  settings put global low_power_mode 0 2>/dev/null",
                    "  settings put global adaptive_refresh_rate 0 2>/dev/null",
                    "  settings put global game_mode_battery_saver 0 2>/dev/null",
                    "  # 游戏模式设置守护（每轮重设）",
                    "      settings put global game_mode_performance 1 2>/dev/null",
                    "      settings put global game_mode_battery 0 2>/dev/null",
                    "      settings put global miui_game_turbo 1 2>/dev/null",
                    "      # 游戏助手复活就再压（每 10 轮=10 秒查一次，降低开销）",
                    "      if [ \$((tick % 30)) = 1 ] && [ -f /data/adb/xy_game_pkgs_disabled ]; then",
                    "        DIS=\$(pm list packages -d 2>/dev/null)",
                    "        while read -r pkg; do",
                    "          [ -z \"\$pkg\" ] && continue",
                    "          echo \"\$DIS\" | grep -q \"package:\$pkg\" || pm disable-user --user 0 \"\$pkg\" >/dev/null 2>&1",
                    "        done < /data/adb/xy_game_pkgs_disabled",
                    "      fi",
                    "      # GPU 守护（高通）：governor 会被改回 msm-adreno-tz",
                    "      for d in /sys/class/kgsl/kgsl-3d0 /sys/class/kgsl/kgsl-3d1; do",
                    "        [ -d \"\$d\" ] || continue",
                    "        if [ -f \"\$d/devfreq/governor\" ]; then",
                    "          g=\$(cat \"\$d/devfreq/governor\" 2>/dev/null)",
                    "          [ \"\$g\" != \"performance\" ] && { chmod 0666 \"\$d/devfreq/governor\" 2>/dev/null; echo performance > \"\$d/devfreq/governor\" 2>/dev/null; }",
                    "        fi",
                    "        if [ -f \"\$d/min_pwrlevel\" ]; then",
                    "          pl=\$(cat \"\$d/min_pwrlevel\" 2>/dev/null)",
                    "          [ \"\$pl\" != \"0\" ] && { chmod 0666 \"\$d/min_pwrlevel\" 2>/dev/null; echo 0 > \"\$d/min_pwrlevel\" 2>/dev/null; }",
                    "        fi",
                    "      done",
                    "      # 先确保 scaling_max 没被压回（否则 min 会被拒）",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_max_freq; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        b=\${f%/scaling_max_freq}",
                    "        hw=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "        [ -n \"\$hw\" ] || continue",
                    "        cur=\$(cat \"\$f\" 2>/dev/null)",
                    "        [ \"\$cur\" != \"\$hw\" ] && { chmod 0666 \"\$f\" 2>/dev/null; echo \"\$hw\" > \"\$f\" 2>/dev/null; }",
                    "      done",
                    "      # GPU OPP 守护（MTK）：被 disable 就重设 0/0",
                    "      for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        v=\$(head -1 \"\$f\" 2>/dev/null)",
                    "        case \"\$v\" in",
                    "          *\"0/0\"*) continue ;;",
                    "          *) chmod 0666 \"\$f\" 2>/dev/null; echo -1 > \"\$f\" 2>/dev/null; echo 0 0 > \"\$f\" 2>/dev/null ;;",
                    "        esac",
                    "      done",
                    "      # CPU 下限守护：系统可能改回，每轮核对（平板常见）",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        b=\${f%/scaling_min_freq}",
                    "        mf=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "        [ -n \"\$mf\" ] || continue",
                    "        want=\$mf",
                    "        cur=\$(cat \"\$f\" 2>/dev/null)",
                    "        [ \"\$cur\" = \"\$want\" ] && continue",
                    "        chmod 0666 \"\$f\" 2>/dev/null",
                    "        echo \"\$want\" > \"\$f\" 2>/dev/null",
                    "      done",
                    "      # nice 守护：被系统重置就重新 renice",
                    "      pidn=\$(pidof \"\$pkg\" 2>/dev/null | awk '{print \$1}')",
                    "      if [ -n \"\$pidn\" ]; then",
                    "        ni=\$(awk '{print \$19}' /proc/\$pidn/stat 2>/dev/null)",
                    "        [ \"\$ni\" != \"-20\" ] && renice -n -20 -p \"\$pidn\" >/dev/null 2>&1",
                    "      fi",
                    "      # 刷新率守护：被游戏/系统改回就重设；没有记录就重新探测",
                    "      MAXF=\$(cat /data/adb/xy_maxf 2>/dev/null)",
                    "      if [ -z \"\$MAXF\" ]; then",
                    "        MAXF=\$(/system/bin/dumpsys display 2>/dev/null | grep -oE \"fps=[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/dumpsys SurfaceFlinger 2>/dev/null | grep -oE \"refresh-rate *[:=] *[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get secure user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null && echo \"\$MAXF\" > /data/adb/xy_maxf",
                    "      fi",
                    "      if [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null; then",
                    "        ccur=\$(refresh_get)",
                    "        if [ \"\$ccur\" != \"\$MAXF\" ]; then",
                    "          refresh_max \"\$MAXF\"",
                    "        fi",
                    "      fi",
                    "      pid=\$(pidof \"\$pkg\" 2>/dev/null | awk '{print \$1}')",
                    "      if [ -n \"\$pid\" ]; then",
                    "        renice -n -20 -p \"\$pid\" >/dev/null 2>&1",
                    "        for t in /proc/\$pid/task/*; do",
                    "          [ -d \"\$t\" ] && renice -n -20 -p \"\${t##*/}\" >/dev/null 2>&1",
                    "        done",
                    "        boost_threads \"\$pid\"",
                    "      fi",
                    "      state=game",
                    "    else",
                    "      # state=game：只做轻量维护（apply_max 只在进入游戏时跑一次）",
                    "      # target_loads 会被系统每几秒改回，需持续维持",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/target_loads; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        v=\$(cat \"\$f\" 2>/dev/null)",
                    "        [ \"\$v\" = \"20\" ] && continue",
                    "        chmod 0666 \"\$f\" 2>/dev/null",
                    "        echo 20 > \"\$f\" 2>/dev/null",
                    "      done",
                    "      # DDR 带宽守护（min=max，被改回就重设）",
                    "      for d in /sys/class/devfreq/*/; do",
                    "        [ -d \"\$d\" ] || continue",
                    "        b=\$(basename \"\$d\")",
                    "        case \"\$b\" in *ddr*|*DDR*|*busmon*|*dvfsrc*|*bw*|*BW*|*memlat*|*cpubw*) ;; *) continue ;; esac",
                    "        case \"\$b\" in *gpu*|*mali*|*kgsl-3d*) continue ;; esac",
                    "        mx=\$(cat \"\$d/max_freq\" 2>/dev/null); [ -n \"\$mx\" ] && [ \"\$mx\" != \"0\" ] || continue",
                    "        cur=\$(cat \"\$d/min_freq\" 2>/dev/null)",
                    "        [ \"\$cur\" = \"\$mx\" ] && continue",
                    "        chmod 0666 \"\$d/min_freq\" 2>/dev/null",
                    "        echo \"\$mx\" > \"\$d/min_freq\" 2>/dev/null",
                    "      done",
                    "      # sched_boost 守护",
                    "      for f in /proc/sys/kernel/sched_boost /dev/stune/top-app/schedtune.boost; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        [ \"\$(cat \"\$f\" 2>/dev/null)\" = \"1\" ] && continue",
                    "        chmod 0644 \"\$f\" 2>/dev/null",
                    "        echo 1 > \"\$f\" 2>/dev/null",
                    "      done",
                    "      # 实际显示模式守护（防系统在 SurfaceFlinger 层切到低刷新率）",
                    "      if [ \$((tick % 10)) = 1 ]; then",
                    "        MAXF=\$(cat /data/adb/xy_maxf 2>/dev/null)",
                    "        if [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null; then",
                    "          ACT=\$(/system/bin/dumpsys display 2>/dev/null | grep -m1 'mActiveSfDisplayMode' | grep -oE 'peakRefreshRate=[0-9.]+' | grep -oE '[0-9]+')",
                    "          if [ -n \"\$ACT\" ] && [ \"\$ACT\" -lt \"\$MAXF\" ] 2>/dev/null; then",
                    "            refresh_max \"\$MAXF\"",
                    "          fi",
                    "        fi",
                    "      fi",
                    "      # thermal zone 策略守护（每轮确保 disabled）",
                    "      for z in /sys/class/thermal/thermal_zone*/mode; do",
                    "        [ -f \"\$z\" ] || continue",
                    "        [ \"\$(cat \"\$z\" 2>/dev/null)\" = \"disabled\" ] && continue",
                    "        chmod 0644 \"\$z\" 2>/dev/null",
                    "        echo disabled > \"\$z\" 2>/dev/null",
                    "      done",
                    "      # 游戏模式设置守护（每轮重设）",
                    "      settings put global game_mode_performance 1 2>/dev/null",
                    "      settings put global game_mode_battery 0 2>/dev/null",
                    "      settings put global miui_game_turbo 1 2>/dev/null",
                    "      # 游戏助手复活就再压（每 10 轮=10 秒查一次，降低开销）",
                    "      if [ \$((tick % 30)) = 1 ] && [ -f /data/adb/xy_game_pkgs_disabled ]; then",
                    "        DIS=\$(pm list packages -d 2>/dev/null)",
                    "        while read -r pkg; do",
                    "          [ -z \"\$pkg\" ] && continue",
                    "          echo \"\$DIS\" | grep -q \"package:\$pkg\" || pm disable-user --user 0 \"\$pkg\" >/dev/null 2>&1",
                    "        done < /data/adb/xy_game_pkgs_disabled",
                    "      fi",
                    "      # GPU 守护（高通）：governor 会被改回 msm-adreno-tz",
                    "      for d in /sys/class/kgsl/kgsl-3d0 /sys/class/kgsl/kgsl-3d1; do",
                    "        [ -d \"\$d\" ] || continue",
                    "        if [ -f \"\$d/devfreq/governor\" ]; then",
                    "          g=\$(cat \"\$d/devfreq/governor\" 2>/dev/null)",
                    "          [ \"\$g\" != \"performance\" ] && { chmod 0666 \"\$d/devfreq/governor\" 2>/dev/null; echo performance > \"\$d/devfreq/governor\" 2>/dev/null; }",
                    "        fi",
                    "        if [ -f \"\$d/min_pwrlevel\" ]; then",
                    "          pl=\$(cat \"\$d/min_pwrlevel\" 2>/dev/null)",
                    "          [ \"\$pl\" != \"0\" ] && { chmod 0666 \"\$d/min_pwrlevel\" 2>/dev/null; echo 0 > \"\$d/min_pwrlevel\" 2>/dev/null; }",
                    "        fi",
                    "      done",
                    "      # 先确保 scaling_max 没被压回（否则 min 会被拒）",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_max_freq; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        b=\${f%/scaling_max_freq}",
                    "        hw=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "        [ -n \"\$hw\" ] || continue",
                    "        cur=\$(cat \"\$f\" 2>/dev/null)",
                    "        [ \"\$cur\" != \"\$hw\" ] && { chmod 0666 \"\$f\" 2>/dev/null; echo \"\$hw\" > \"\$f\" 2>/dev/null; }",
                    "      done",
                    "      # GPU OPP 守护（MTK）：被 disable 就重设 0/0",
                    "      for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        v=\$(head -1 \"\$f\" 2>/dev/null)",
                    "        case \"\$v\" in",
                    "          *\"0/0\"*) continue ;;",
                    "          *) chmod 0666 \"\$f\" 2>/dev/null; echo -1 > \"\$f\" 2>/dev/null; echo 0 0 > \"\$f\" 2>/dev/null ;;",
                    "        esac",
                    "      done",
                    "      # CPU 下限守护：系统可能改回，每轮核对（平板常见）",
                    "      for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do",
                    "        [ -f \"\$f\" ] || continue",
                    "        b=\${f%/scaling_min_freq}",
                    "        mf=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null)",
                    "        [ -n \"\$mf\" ] || continue",
                    "        want=\$mf",
                    "        cur=\$(cat \"\$f\" 2>/dev/null)",
                    "        [ \"\$cur\" = \"\$want\" ] && continue",
                    "        chmod 0666 \"\$f\" 2>/dev/null",
                    "        echo \"\$want\" > \"\$f\" 2>/dev/null",
                    "      done",
                    "      # nice 守护：被系统重置就重新 renice",
                    "      pidn=\$(pidof \"\$pkg\" 2>/dev/null | awk '{print \$1}')",
                    "      if [ -n \"\$pidn\" ]; then",
                    "        ni=\$(awk '{print \$19}' /proc/\$pidn/stat 2>/dev/null)",
                    "        [ \"\$ni\" != \"-20\" ] && renice -n -20 -p \"\$pidn\" >/dev/null 2>&1",
                    "      fi",
                    "      # 刷新率守护：被游戏/系统改回就重设；没有记录就重新探测",
                    "      MAXF=\$(cat /data/adb/xy_maxf 2>/dev/null)",
                    "      if [ -z \"\$MAXF\" ]; then",
                    "        MAXF=\$(/system/bin/dumpsys display 2>/dev/null | grep -oE \"fps=[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/dumpsys SurfaceFlinger 2>/dev/null | grep -oE \"refresh-rate *[:=] *[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get system user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -z \"\$MAXF\" ] && MAXF=\$(/system/bin/settings get secure user_refresh_rate 2>/dev/null | cut -d. -f1)",
                    "        [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null && echo \"\$MAXF\" > /data/adb/xy_maxf",
                    "      fi",
                    "      if [ -n \"\$MAXF\" ] && [ \"\$MAXF\" -ge 60 ] 2>/dev/null; then",
                    "        ccur=\$(refresh_get)",
                    "        if [ \"\$ccur\" != \"\$MAXF\" ]; then",
                    "          refresh_max \"\$MAXF\"",
                    "        fi",
                    "      fi",
                    "      pid=\$(pidof \"\$pkg\" 2>/dev/null | awk '{print \$1}')",
                    "      if [ -n \"\$pid\" ]; then",
                    "        renice -n -20 -p \"\$pid\" >/dev/null 2>&1",
                    "        for t in /proc/\$pid/task/*; do",
                    "          [ -d \"\$t\" ] && renice -n -20 -p \"\${t##*/}\" >/dev/null 2>&1",
                    "        done",
                    "        boost_threads \"\$pid\"",
                    "      fi",
                    "    fi",
                    "  else",
                    "    if [ \"\$state\" = \"game\" ] || [ \"\$temp\" -ge 92000 ] 2>/dev/null; then",
                    "      restore_default",
                    "      state=normal",
                    "    fi",
                    "  fi",
                    "  # 非游戏状态：每 5 秒兜底恢复被禁用的包（防上次异常退出残留）",
                    "  if [ \"\$state\" != \"game\" ] && [ \$((tick % 10)) = 1 ]; then",
                    "    DIS=\$(pm list packages -d 2>/dev/null)",
                    "    for p in \$XY_PKGS; do",
                    "      echo \"\$DIS\" | grep -q \"package:\$p\" && pm enable --user 0 \"\$p\" >/dev/null 2>&1",
                    "    done",
                    "  fi",
                    "",
                    "  sleep 1",
                    "done",
                    ""
                                    )
                                    su("cat > /data/adb/xy_game.sh <<'XYEOF'\n" + gameLines.joinToString("\n") + "\nXYEOF\nchmod 755 /data/adb/xy_game.sh")
                                    val checkLines = listOf(
                    "#!/system/bin/sh",
                    "OK=0; BAD=0",
                    "pass() { echo \"✓ \$1\"; OK=\$((OK+1)); }",
                    "fail() { echo \"✗ \$1\"; BAD=\$((BAD+1)); }",
                    "info() { echo \"• \$1\"; }",
                    "",
                    "pgrep -f xy_game.sh >/dev/null 2>&1 && pass \"守护运行中\" || fail \"守护未运行\"",
                    "[ -f /data/adb/xy_game.sh ] && pass \"加速脚本就位\" || fail \"加速脚本缺失\"",
                    "[ -f /data/adb/service.d/xy_game.sh ] && pass \"开机自启就位\" || fail \"开机自启缺失\"",
                    "",
                    "pkg=\$(dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity | sed -E 's/.* ([a-zA-Z0-9_.]+)\\/.*/\\1/')",
                    "g=\"\"",
                    "case \"\$pkg\" in",
                    "  com.tencent.tmgp.*|com.tencent.ig|com.pubg.imobile|com.rekoo.pubgm|com.tencent.mf.uam|com.tencent.KiHan|com.miHoYo.*|com.kurogame.*|com.activision.*) g=1 ;;",
                    "esac",
                    "",
                    "if [ -n \"\$g\" ]; then",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    b=\${f%/scaling_min_freq}",
                    "    mf=\$(cat \"\$b/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$mf\" ] || continue",
                    "    cur=\$(cat \"\$f\" 2>/dev/null); pct=\$((cur * 100 / mf))",
                    "    [ \"\$pct\" -ge 35 ] && pass \"min_freq \$(basename \$b) = \$pct%\" || fail \"min_freq \$(basename \$b) = \$pct%\"",
                    "  done",
                    "  for f in /sys/devices/system/cpu/cpufreq/policy*/sugov_ext/target_loads; do",
                    "    [ -f \"\$f\" ] || continue",
                    "    [ \"\$(cat \"\$f\")\" = \"30\" ] && pass \"target_loads \$(basename \$(dirname \$(dirname \$f))) = 30\" || fail \"target_loads 不是 30\"",
                    "  done",
                    "  case \"\$(cat /proc/gpufreqv2/fix_target_opp_index 2>/dev/null)\" in",
                    "    *0/0*) pass \"GPU 已锁最高\" ;;",
                    "    *) [ -f /sys/class/kgsl/kgsl-3d0/devfreq/governor ] && pass \"GPU governor 已设\" || fail \"GPU 未锁\" ;;",
                    "  esac",
                    "else",
                    "  info \"当前前台: \${pkg:-未知}（非游戏）\"",
                    "  info \"参数交回系统，这是正常状态\"",
                    "  pass \"非游戏模式（不占资源）\"",
                    "fi",
                    "",
                    "t=0",
                    "for z in /sys/class/thermal/thermal_zone*/; do",
                    "  n=\$(cat \"\$z/type\" 2>/dev/null)",
                    "  case \"\$n\" in soc_max) t=\$(cat \"\$z/temp\" 2>/dev/null) ;; esac",
                    "done",
                    "[ -n \"\$t\" ] && [ \"\$t\" -gt 0 ] && info \"SoC \$((t/1000))°C\"",
                    "",
                    "echo \"通过 \$OK 项，失败 \$BAD 项\"",
                    ""
                                    )
                                    su("cat > /data/adb/service.d/xy_check.sh <<'XYEOF'\n" + checkLines.joinToString("\n") + "\nXYEOF\nchmod 755 /data/adb/service.d/xy_check.sh")
                                    su("printf '#!/system/bin/sh\\nsetsid sh /data/adb/xy_game.sh >/dev/null 2>&1 &\\n' > /data/adb/service.d/xy_game.sh ; chmod 755 /data/adb/service.d/xy_game.sh")
                                    su("kill \$(cat /data/adb/xy_game.pid 2>/dev/null) 2>/dev/null; sleep 0.3; setsid sh /data/adb/xy_game.sh >/dev/null 2>&1 &")
                                    "已开启"
                                } else {
                                    su("kill \$(cat /data/adb/xy_game.pid 2>/dev/null) 2>/dev/null; rm -f /data/adb/xy_game.sh /data/adb/service.d/xy_game.sh")
                                    su("for f in /sys/devices/system/cpu/cpufreq/policy*/scaling_min_freq; do b=\${f%/scaling_min_freq}; m=\$(cat \$b/cpuinfo_min_freq 2>/dev/null); [ -n \"\$m\" ] && echo \$m > \$f 2>/dev/null; done; settings delete system peak_refresh_rate 2>/dev/null; settings delete system min_refresh_rate 2>/dev/null")
                                    "已关闭"
                                }
                            } catch (t: Throwable) {
                                "操作失败"
                            }
                        }
                        latencyMsg = msg
                    }
                }
                if (title == "放开通行") {
                    val enabling = on
                    scope2.launch {
                        withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf(suBin(), "-c", c))
                                    val out = p.inputStream.bufferedReader().readText()
                                    p.waitFor()
                                    return out
                                }
                                if (enabling) {
                                    // install a boot script so the state survives reboots
                                    try {
                                        val bootDir = "/data/adb/service.d"
                                        su("mkdir -p $bootDir")
                                        val boot = "#!/system/bin/sh\n" +
                                            "chmod 771 /data/adb\n" +
                                            "chmod 755 /data/adb/modules\n" +
                                            "chmod 755 /data/adb/ksu\n" +
                                            "chmod 755 /data/adb/ksud\n" +
                                            "printf 'allow untrusted_app adb_data_file dir { open read search getattr }\\n' > /data/local/tmp/xy_allow.txt\n" +
                                            "printf 'allow untrusted_app adb_data_file file { open read getattr }\\n' >> /data/local/tmp/xy_allow.txt\n" +
                                            "n=0\n" +
                                            "while [ \"\$n\" -lt 30 ]; do\n" +
                                            "  done_ok=0\n" +
                                            "  for c in /data/adb/ksud /data/adb/ksu/bin/ksud; do\n" +
                                            "    [ -x \"\$c\" ] || continue\n" +
                                            "    \"\$c\" sepolicy apply /data/local/tmp/xy_allow.txt >/dev/null 2>&1 && done_ok=1\n" +
                                            "  done\n" +
                                            "  [ \"\$done_ok\" = \"1\" ] && break\n" +
                                            "  n=\$((n + 1))\n" +
                                            "  sleep 2\n" +
                                            "done\n"
                                        su("cat > $bootDir/xy_open_access.sh <<'XYBOOT'\n" + boot + "XYBOOT\nchmod 755 $bootDir/xy_open_access.sh")
                                    } catch (t: Throwable) {
                                    }
                                    for ((path, mode) in listOf(
                                        "/data/adb" to "771",
                                        "/data/adb/modules" to "755",
                                        "/data/adb/ksu" to "755",
                                        "/data/adb/ksud" to "755"
                                    )) {
                                        su("chmod $mode '" + path.replace("'", "'\''") + "' 2>/dev/null; true")
                                    }
                                    val domains = listOf(
                                        "untrusted_app", "untrusted_app_25", "untrusted_app_27",
                                        "untrusted_app_29", "untrusted_app_30", "untrusted_app_32",
                                        "untrusted_app_34"
                                    )
                                    val rules = domains.joinToString("\n") { dm ->
                                        "allow " + dm + " adb_data_file dir { open read search getattr }\n" +
                                            "allow " + dm + " adb_data_file file { open read getattr }\n" +
                                            "allow " + dm + " adb_data_file lnk_file read\n" +
                                            "allow " + dm + " proc_modules file { open read getattr }"
                                    } + "\n"
                                    val rf = "/data/local/tmp/sepolicy_rules.txt"
                                    su("printf '%s' '" + rules.replace("'", "'\''") + "' > $rf ; chmod 644 $rf ; true")
                                    val ks = ctx3.applicationInfo.nativeLibraryDir + "/libksud.so"
                                    if (java.io.File(ks).exists()) {
                                        su("'" + ks.replace("'", "'\\''") + "' sepolicy apply $rf 2>&1; true")
                                    }
                                } else {
                                    try {
                                        su("rm -f /data/adb/service.d/xy_open_access.sh")
                                    } catch (t: Throwable) {
                                    }
                                    for (path in listOf(
                                        "/data/adb", "/data/adb/modules",
                                        "/data/adb/ksu", "/data/adb/ksud"
                                    )) {
                                        su("chmod 700 '" + path.replace("'", "'\''") + "' 2>/dev/null; true")
                                    }
                                    val rules = "deny untrusted_app adb_data_file dir { open read search getattr }\n" +
                                        "deny untrusted_app adb_data_file file { open read getattr }\n" +
                                        "deny untrusted_app adb_data_file lnk_file read\n" +
                                        "deny untrusted_app proc_modules file { open read getattr }\n"
                                    val rf = "/data/local/tmp/sepolicy_deny.txt"
                                    su("printf '%s' '" + rules.replace("'", "'\''") + "' > $rf ; chmod 644 $rf ; true")
                                    val ks = ctx3.applicationInfo.nativeLibraryDir + "/libksud.so"
                                    if (java.io.File(ks).exists()) {
                                        su("'" + ks.replace("'", "'\\''") + "' sepolicy apply $rf 2>&1; true")
                                    }
                                }
                            } catch (t: Throwable) {
                            }
                        }
                        kotlinx.coroutines.delay(600)
                        refreshLatency()
                    }
                }
            }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            Text(
                text = subtitle ?: (execPath ?: latencyMsg)
                    ?: (if (title == "执行此文件" || title == "刷入驱动") (execPath ?: "暂无设置") else ""),
                color = Color(0x66FFFFFF),
                fontSize = 9.sp,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .then(
                        if (title == "执行此文件" || title == "刷入驱动") {
                            Modifier.clickable {
                                pathInput = execPath ?: ""
                                showPathDialog = true
                            }
                        } else Modifier
                    )
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "开",
                color = if (on) Color.White else Color(0xFF5A5A5E),
                fontSize = 15.sp,
                fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(text = " | ", color = Color(0xFF6A6A6E), fontSize = 15.sp)
            Text(
                text = "关",
                color = if (!on) Color.White else Color(0xFF5A5A5E),
                fontSize = 15.sp,
                fontWeight = if (!on) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
    if (showUrlDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showUrlDialog = false },
            title = { androidx.compose.material3.Text("驱动 zip 直链") },
            text = {
                androidx.compose.foundation.text.BasicTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val u = urlInput.trim()
                    ctx3.getSharedPreferences("yx_exec", 0).edit().putString("rt_url", u).apply()
                    showUrlDialog = false
                    if (u.isNotEmpty()) {
                        latencyMsg = "下载中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val dir = java.io.File("/storage/emulated/0/小鱼儿")
                                    dir.mkdirs()
                                    val zip = java.io.File(dir, "rtdrv.zip")
                                    val conn = java.net.URL(u).openConnection() as java.net.HttpURLConnection
                                    conn.connectTimeout = 20000
                                    conn.readTimeout = 120000
                                    conn.instanceFollowRedirects = true
                                    conn.inputStream.use { input ->
                                        zip.outputStream().use { out -> input.copyTo(out) }
                                    }
                                    val pr = ProcessBuilder(
                                        suBin(), "-c",
                                        "cd '/storage/emulated/0/小鱼儿' && " +
                                            "(unzip -o -q rtdrv.zip 2>/dev/null || busybox unzip -o rtdrv.zip 2>/dev/null || tar -xf rtdrv.zip) ; " +
                                            "chmod 644 /storage/emulated/0/小鱼儿/*.sh 2>/dev/null ; ls /storage/emulated/0/小鱼儿/*.sh 2>/dev/null | wc -l"
                                    ).redirectErrorStream(true).start()
                                    val out = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    val n = out.trim().lines().lastOrNull()?.trim() ?: "?"
                                    "已下载并解压 $n 个脚本"
                                } catch (t: Throwable) {
                                    "下载失败：" + (t.message ?: "")
                                }
                            }
                            latencyMsg = msg
                        }
                    }
                }) { androidx.compose.material3.Text("下载") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showUrlDialog = false }) {
                    androidx.compose.material3.Text("取消")
                }
            }
        )
    }
    if (showPathDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPathDialog = false },
            title = { androidx.compose.material3.Text("执行文件路径") },
            text = {
                androidx.compose.foundation.text.BasicTextField(
                    value = pathInput,
                    onValueChange = { pathInput = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val v = pathInput.trim()
                    val lineNo = when (slot) { "path" -> 3; "path2" -> 4; "path3" -> 5; else -> 3 }
                    execPath = v
                    showPathDialog = false
                    scope2.launch {
                        withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val sv = v.replace("'", "'\\''")
                                val awk = "awk -v n=$lineNo -v v='" + sv + "' 'NR==n{print v; f=1; next} {print} END{if(!f) for(i=NR+1;i<=n;i++) print (i==n?v:\"\")}'"
                                val cmd = "F='/storage/emulated/0/小鱼儿/小鱼儿密钥' ; mkdir -p '/storage/emulated/0/小鱼儿' ; " +
                                    "[ -f \"\$F\" ] || : > \"\$F\" ; " +
                                    awk + " \"\$F\" > /data/local/tmp/xy_kf && cat /data/local/tmp/xy_kf > \"\$F\" ; rm -f /data/local/tmp/xy_kf ; chmod 600 \"\$F\""
                                Runtime.getRuntime().exec(arrayOf(suBin(), "-c", cmd)).waitFor()
                            } catch (t: Throwable) {
                            }
                        }
                    }
                }) { androidx.compose.material3.Text("确定") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showPathDialog = false }) {
                    androidx.compose.material3.Text("取消")
                }
            }
        )
    }
    if (showKeyDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showKeyDialog = false },
            title = { androidx.compose.material3.Text("so 密钥") },
            text = {
                androidx.compose.foundation.text.BasicTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    val p2 = ctx3.getSharedPreferences("yx_exec", 0).getString("path", null)
                    showKeyDialog = false
                    if (!p2.isNullOrBlank()) {
                        latencyMsg = "执行中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "-c",
                                        "export HOME=/data/local/tmp; export TMPDIR=/data/local/tmp; " +
                                            "cd '" + java.io.File(p2).parent + "' 2>/dev/null; " +
                                            "chmod 755 '" + p2.replace("'", "'\\''") + "' 2>/dev/null; " +
                                            "'" + p2.replace("'", "'\\''") + "' -k '" + keyInput.replace("'", "'\\''") + "' 2>&1 | tail -n 2"))
                                    val o = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    if (o.contains("fail") || o.contains("error")) "执行失败" else "已执行 so"
                                } catch (t: Throwable) {
                                    "执行异常"
                                }
                            }
                            latencyMsg = msg
                        }
                    }
                }) { androidx.compose.material3.Text("执行") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showKeyDialog = false }) {
                    androidx.compose.material3.Text("取消")
                }
            }
        )
    }
}

@Composable
private fun BrowserPane(
    path: String,
    onNavigate: (String) -> Unit,
    onFileClick: (String) -> Unit,
    refreshTick: Int = 0,
    selectedPath: String? = null,
    highlightName: String? = null,
    onLongPress: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var entries by remember { mutableStateOf<List<FsEntry>>(emptyList()) }
    var pullDy by remember { mutableStateOf(0f) }
    val refreshRef = androidx.compose.runtime.rememberUpdatedState(onRefresh)
    val pullConn = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0f) {
                    pullDy += available.y
                    if (pullDy > 150f) {
                        pullDy = 0f
                        refreshRef.value()
                    }
                } else if (available.y < 0f) {
                    pullDy = 0f
                }
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(path, refreshTick) {
        entries = listDir(path)
    }

    val parent = parentOf(path)
    val listState = rememberLazyListState()
    LaunchedEffect(highlightName, entries) {
        val idx = entries.indexOfFirst { it.name == highlightName }
        if (idx >= 0) {
            listState.animateScrollToItem(idx + 1)
        }
    }

        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize().background(Color(BG_COLOR)).nestedScroll(pullConn),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = parent != path) { onNavigate(parent) }
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(TILE_COLOR)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = Color(FOLDER_ICON),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "..", color = Color(TEXT_COLOR), fontSize = 15.sp)
            }
        }
        items(entries) { entry ->
            val rowPath = joinPath(path, entry.name)
            FsRow(
                name = entry.name,
                isDir = entry.isDir,
                fullPath = rowPath,
                selected = selectedPath == rowPath,
                highlight = highlightName,
                onClick = {
                    if (entry.isDir) onNavigate(joinPath(path, entry.name)) else onFileClick(joinPath(path, entry.name))
                },
                onLongClick = { onLongPress(rowPath) }
            )
        }
    }
}

@Composable
private fun FsRow(
    name: String,
    isDir: Boolean,
    fullPath: String,
    selected: Boolean = false,
    highlight: String? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    // ZySU row: rounded 14dp, no tile behind the glyph, 12sp monospace, outline on the row
    val accent = if (isDir) null else fileAccent(name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(
                if (highlight != null && highlight == name) Color(0x3300BCD4)
                else Color.Transparent
            )
            .then(
                if (accent != null) {
                    Modifier.border(1.dp, accent.copy(alpha = 0.38f), RoundedCornerShape(14.dp))
                } else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RowIcon(isDir = isDir, name = name, fullPath = fullPath)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = FontFamily.Monospace,
            color = Color(TEXT_COLOR)
        )
    }
}

private fun fileAccent(name: String): Color? {
    val l = name.lowercase()
    return when {
        l.endsWith(".apk") -> Color(0xFFFFB6C8)
        l.endsWith(".so") -> Color(0xFF4A9EFF)
        l.endsWith(".sh") || l.endsWith(".rc") -> Color(0xFF26C6DA)
        l.endsWith(".zip") || l.endsWith(".tar") || l.endsWith(".gz") ||
            l.endsWith(".7z") || l.endsWith(".rar") || l.endsWith(".xz") -> Color(0xFFFFCA24)
        else -> null
    }
}

@Composable
private fun RowIcon(isDir: Boolean, name: String, fullPath: String) {
    val ctx = LocalContext.current

    val apkIcon by produceState<ImageBitmap?>(apkIconCache[fullPath], fullPath) {
        if (value != null) return@produceState
        if (isDir || !isApk(name)) return@produceState
        value = withContext(Dispatchers.IO) {
            apkIconMutex.withLock {
                apkIconCache.getOrPut(fullPath) { loadApkIcon(ctx, fullPath) }
            }
        }
    }

    val apkIconNow = apkIcon
    if (apkIconNow != null) {
        Image(
            bitmap = apkIconNow,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    } else {
        Icon(
            imageVector = if (isDir) Icons.Outlined.Folder else Icons.Outlined.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isDir) Color(FOLDER_ICON) else (fileAccent(name) ?: Color(FILE_ICON))
        )
    }
}

private fun loadApkIcon(ctx: Context, path: String): ImageBitmap? {
    return try {
        val pm = ctx.packageManager
        var usedPath = path
        var info = pm.getPackageArchiveInfo(path, 0)

        if (info == null) {
            val probe = ctx.cacheDir.absolutePath + "/icon_probe_" + Math.abs(path.hashCode()) + ".apk"
            copyOutForIcon(path, probe)
            usedPath = probe
            info = pm.getPackageArchiveInfo(probe, 0)
        }

        val app = info?.applicationInfo
        if (app == null) null else {
            app.sourceDir = usedPath
            app.publicSourceDir = usedPath
            try {
                val drawable = app.loadIcon(pm)
                val bmp = if (drawable is android.graphics.drawable.BitmapDrawable && drawable.bitmap != null) {
                    drawable.bitmap
                } else {
                    val w = drawable.intrinsicWidth.coerceAtLeast(1)
                    val h = drawable.intrinsicHeight.coerceAtLeast(1)
                    val b = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(b)
                    drawable.setBounds(0, 0, w, h)
                    drawable.draw(canvas)
                    b
                }
                bmp.asImageBitmap()
            } catch (t: Throwable) {
                null
            }
        }
    } catch (t: Throwable) {
        null
    }
}

private fun copyOutForIcon(src: String, dest: String) {
    try {
        val p = Runtime.getRuntime().exec(
            arrayOf(suBin(), "-c", "cp " + shq(src) + " " + shq(dest) + " && chmod 644 " + shq(dest))
        )
        p.waitFor()
    } catch (t: Throwable) {
    }
}

private fun shq(s: String): String = "'" + s.replace("'", "'\''") + "'"

internal fun isApk(n: String): Boolean {
    val lower = n.lowercase()
    if (lower.endsWith(".apk")) return true
    val tail = lower.substringAfterLast(".apk.", "")
    return tail.isNotEmpty() && tail.all { it.isDigit() }
}

private suspend fun listDir(path: String): List<FsEntry> = withContext(Dispatchers.IO) {
    val norm = if (path.endsWith("/")) path else "$path/"

    // go straight through su: libsu may be uninitialised and silently yields an empty list
    val shellOut = suNow("ls -1p \"$norm\" 2>/dev/null")
        .lines().filter { it.isNotBlank() }

    val fromShell = shellOut
        .filter { it.isNotBlank() }
        .map { line ->
            val trimmed = line.trim()
            FsEntry(trimmed.removeSuffix("/"), trimmed.endsWith("/"))
        }

    if (fromShell.isNotEmpty()) {
        fromShell.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
    } else {
        runCatching { File(norm).listFiles() }.getOrNull()
            ?.map { FsEntry(it.name, it.isDirectory) }
            ?.sortedWith(compareByDescending<FsEntry> { it.isDir }.thenBy { it.name.lowercase() })
            ?: emptyList()
    }
}

private fun parentOf(path: String): String {
    val p = if (path.length > 1 && path.endsWith("/")) path.dropLast(1) else path
    val i = p.lastIndexOf('/')
    return if (i <= 0) "/" else p.substring(0, i)
}

private fun joinPath(base: String, name: String): String =
    if (base.endsWith("/")) "$base$name" else "$base/$name"

private const val RT_DRIVER_URL = "https://github.com/thy6070/KernelSU-Next/releases/download/rtdrv-v1/xiaoyuer_rtdrv.zip"
private val RT_DRIVER_MIRRORS = listOf(
    "https://ghfast.top/",
    "https://gh-proxy.com/",
    "https://ghproxy.net/",
    "https://gh.llkk.cc/",
    "https://hub.gitmirror.com/"
)

internal fun suBin(): String {
    // Never probe at length here: the first su call may block on the grant dialog.
    // Just pick the first candidate that exists, else let the shell resolve "su".
    for (c in listOf("/data/adb/ksu/bin/su", "/data/adb/magisk/su",
                     "/system/bin/su", "/system/xbin/su")) {
        try {
            if (java.io.File(c).exists()) return c
        } catch (t: Throwable) {
        }
    }
    return "su"
}

/** Runs a root command with a bounded wait so a hidden grant dialog cannot stall the UI. */
internal fun suNow(cmd: String, timeoutMs: Long = 20000L): String {
    return try {
        val pr = ProcessBuilder(suBin(), "-c", cmd).redirectErrorStream(true).start()
        val done = java.util.concurrent.Executors.newSingleThreadExecutor().submit<Boolean> {
            pr.waitFor(); true
        }.get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
        val out = pr.inputStream.bufferedReader().readText()
        if (done) out else ""
    } catch (t: Throwable) {
        ""
    }
}
