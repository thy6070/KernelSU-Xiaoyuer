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
                                arrayOf(suBin(), "--mount-master", "-c", "test -d '" + lp.replace("'", "'\\''") + "' && echo y")
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
                                arrayOf(suBin(), "--mount-master", "-c", "test -d '" + lp.replace("'", "'\\''") + "' && echo y")
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
                        ProcessBuilder(suBin(), "--mount-master", "-c", cmd).redirectErrorStream(true).start().waitFor()
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
                                val proc = ProcessBuilder(suBin(), "--mount-master", "-c", cmd)
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
                                        suBin(), "--mount-master", "-c",
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
                    // 和 KSU 管理器一致：读 id → 清理残留 → ksud 原版安装 → 验证
                    val q = "'" + p.replace("'", "'\''") + "'"
                    val cmd = "ZID=$(unzip -p " + q + " module.prop 2>/dev/null | grep '^id=' | cut -d= -f2 | tr -d '\r') ; " +
                        "[ -n \"\$ZID\" ] && { rm -rf \"/data/adb/modules/\$ZID\" \"/data/adb/modules_update/\$ZID\" ; } ; " +
                        "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                        "\"/data/adb/ksud\" module install " + q + " 2>&1 ; " +
                        "echo '--- verify:' ; " +
                        "if [ -f \"/data/adb/modules/\$ZID/module.prop\" ] || [ -f \"/data/adb/modules_update/\$ZID/module.prop\" ]; then " +
                        "echo \"[OK] \$ZID installed\" ; else echo \"[FAIL] \$ZID incomplete\" ; fi"
                    try {
                        val bb = listOf(
                            "/data/adb/ksu/bin/busybox",
                            "/data/adb/magisk/busybox",
                            "/system/bin/busybox",
                            "/system/xbin/busybox",
                            "/data/adb/modules/busybox-ndk/system/bin/busybox"
                        ).firstOrNull { java.io.File(it).isFile }
                        // 用数组形式（不经过 shell 引号拼接，避免嵌套单引号破坏）
                        withContext(Dispatchers.IO) {
                            val pb = ProcessBuilder(suBin(), "--mount-master", "-c", cmd + " 2>&1")
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
                            // 诊断：输出分支判断
                            val _dbg = "p=" + p + " lower=" + lower + " isZip=" + lower.endsWith(".zip") + " isSh=" + lower.endsWith(".sh") + "\n"
                            run { }
                            val cmd = when {
                                lower.endsWith(".apk") ->
                                    "cp -f " + shq(p) + " /data/local/tmp/xy_install.apk ; " +
                                        "chmod 644 /data/local/tmp/xy_install.apk ; " +
                                        "pm install -r /data/local/tmp/xy_install.apk"
                                lower.endsWith(".zip") -> {
                                    // 模块 zip：走官方 ksud（/data/adb/ksud 优先）
                                    // 用 shell 判断（root 能查 /data/adb）；app 的 canExecute 会误判
                                    val libKsud = appCtx.applicationInfo.nativeLibraryDir + "/libksud.so"
                                    val ksudZip = "/data/adb/ksud"
                                    // 1) 从 zip 读模块 id → 清理残留（防残缺安装）2) ksud 安装 3) 验证
                                    "ZID=$(unzip -p " + shq(p) + " module.prop 2>/dev/null | grep '^id=' | cut -d= -f2 | tr -d '\r') ; " +
                                        "[ -n \"\$ZID\" ] && { rm -rf \"/data/adb/modules/\$ZID\" \"/data/adb/modules_update/\$ZID\" ; } ; " +
                                        "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                                        "\"/data/adb/ksud\" module install " + shq(p) + " 2>&1 ; " +
                                        "echo '--- verify:' ; " +
                                        "if [ -f \"/data/adb/modules/\$ZID/module.prop\" ] || [ -f \"/data/adb/modules_update/\$ZID/module.prop\" ]; then " +
                                        "echo \"[OK] \$ZID installed\" ; else echo \"[FAIL] \$ZID incomplete\" ; fi"
                                }
                                lower.endsWith(".so") -> {
                                    val key = soKey.trim()
                                    "cp -f \"$p\" /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                        "chmod 755 /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                        "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                                        (if (key.isEmpty()) "/data/local/tmp/xy_exec.so" else "/data/local/tmp/xy_exec.so -k \"$key\"") +
                                        " 2>&1 ; rm -f /data/local/tmp/xy_exec.so"
                                }
                                else -> {
                                    // /data 等 app 读不到 → 先 root 复制到缓存，检测副本是否 ELF
                                    val dst2 = java.io.File(appCtx.cacheDir, "xy_probe.bin")
                                    try {
                                        val cp = ProcessBuilder(suBin(), "--mount-master", "-c",
                                            "cp -f " + shq(p) + " " + shq(dst2.absolutePath) + " ; chmod 644 " + shq(dst2.absolutePath))
                                        cp.redirectErrorStream(true); cp.start().waitFor()
                                    } catch (e: Throwable) { }
                                    val elfMagic = try {
                                        val b = ByteArray(4)
                                        java.io.FileInputStream(dst2).use { it.read(b) }
                                        b[0] == 0x7F.toByte() && b[1] == 'E'.code.toByte() && b[2] == 'L'.code.toByte() && b[3] == 'F'.code.toByte()
                                    } catch (e: Throwable) { false }
                                    if (elfMagic) {
                                        "cp -f " + shq(p) + " /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                            "chmod 755 /data/local/tmp/xy_exec.so 2>/dev/null ; " +
                                            "export TERM=xterm-256color ; /data/local/tmp/xy_exec.so 2>&1 ; " +
                                            "rm -f /data/local/tmp/xy_exec.so"
                                    } else {
                                        "cp -f " + shq(p) + " /data/local/tmp/xy_exec.sh 2>/dev/null ; " +
                                            "chmod 755 /data/local/tmp/xy_exec.sh 2>/dev/null ; " +
                                            "export TERM=xterm-256color ; sh /data/local/tmp/xy_exec.sh 2>&1 ; " +
                                            "rm -f /data/local/tmp/xy_exec.sh"
                                    }
                                }
                            }
                            withContext(Dispatchers.Main) { termText = termText + "\n[debug] " + _dbg }
                            val pb = ProcessBuilder(suBin(), "--mount-master", "-c", cmd)
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
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", cmd))
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
                if (prefs.getBoolean("open", false)) {
                    Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                painter = painterResource(id = me.weishu.kernelsu.R.mipmap.mod_icon),
                contentDescription = null,
                modifier = Modifier.size(34.dp)
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
                    painter = painterResource(id = me.weishu.kernelsu.R.mipmap.ic_modules_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp).clip(RoundedCornerShape(percent = 22))
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
                                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
                                            Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                                                            Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                                                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                                                                Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
                                        Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", cmd)).waitFor()
                                        manual
                                    } else {
                                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
            // 小字：直接读 /storage/emulated/0/小鱼儿/小鱼儿密钥 第 6 行（脚本开启时写入机型名）
            var devLabel by androidx.compose.runtime.remember {
                androidx.compose.runtime.mutableStateOf("")
            }
            androidx.compose.runtime.LaunchedEffect(Unit) {
                devLabel = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                            "sed -n '6p' '/storage/emulated/0/小鱼儿/小鱼儿密钥' 2>/dev/null"))
                        val v = pr.inputStream.bufferedReader().readText().trim()
                        pr.waitFor()
                        if (v.isEmpty()) "未适配此机型" else v
                    } catch (t: Throwable) { "未适配此机型" }
                }
            }
            ModeButton(Icons.Outlined.DarkMode, "放开通行")
            ModeButton(Icons.Outlined.AcUnit, "性能模式", subtitle = devLabel)
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

private fun loadExecPath(slot: String): String? {
    return try {
        val lineNo = when (slot) { "path" -> 3; "path2" -> 4; "path3" -> 5; else -> 3 }
        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
            "sed -n '" + lineNo + "p' '/storage/emulated/0/小鱼儿/小鱼儿密钥' 2>/dev/null"))
        val o = pr.inputStream.bufferedReader().readText().trim()
        pr.waitFor()
        o.ifEmpty { null }
    } catch (t: Throwable) {
        null
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
    var clickTick by remember { mutableStateOf(0) }
    var onInitDone by remember { mutableStateOf(false) }
    // 每次进入界面：检测守护文件是否存在（存在=开，不存在=关）
    LaunchedEffect(Unit) {
        val running = withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching {
                val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                    "if [ -f /data/adb/xy_perf_daemon ]; then echo 1; else echo 0; fi"))
                val o = pr.inputStream.bufferedReader().readText().trim(); pr.waitFor()
                o.trim() == "1"
            }.getOrDefault(false)
        }
        on = running
        onInitDone = true
    }
    var execPath by remember { mutableStateOf<String?>(null) }
    val ctxMsg = androidx.compose.ui.platform.LocalContext.current
    var latencyMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        latencyMsg = ctxMsg.getSharedPreferences("yx_mode", 0).getString("msg_" + title, null)
        // 进界面就读取"执行此文件"的路径（不用点击）
        if (title == "执行此文件") {
            execPath = withContext(kotlinx.coroutines.Dispatchers.IO) { loadExecPath(slot) }
        }
        // 进界面就检测"放开通行"状态（不用点击）
        if (title == "放开通行") {
            val ex = withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                        "[ -f /data/adb/service.d/xy_open_access.sh ] && echo 1 || echo 0"))
                    val o = pr.inputStream.bufferedReader().readText().trim(); pr.waitFor()
                    o == "1"
                }.getOrDefault(false)
            }
            on = ex
        }
    }
    LaunchedEffect(latencyMsg) {
        val v = latencyMsg
        if (v != null) {
            ctxMsg.getSharedPreferences("yx_mode", 0).edit().putString("msg_" + title, v).apply()
        }
    }
    val ctx3 = androidx.compose.ui.platform.LocalContext.current
    val scope2 = rememberCoroutineScope()
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
    // 首次进入跳过，只有用户点击（on 变化）才执行
    var modeFirstRun by remember { mutableStateOf(true) }
    LaunchedEffect(title, clickTick) {
        if (clickTick == 0) return@LaunchedEffect
        if (title == "性能模式") {
            val enabling = on
            // 未适配机型：不开启（脚本也有兜底）
            val mdl = runCatching {
                val pr0 = Runtime.getRuntime().exec(arrayOf("getprop", "ro.product.model"))
                val v = pr0.inputStream.bufferedReader().readText().trim(); pr0.waitFor(); v
            }.getOrDefault("")
            val supported = mdl in listOf(
                "PLF110", "PKG110",                                  // 一加 Ace5 竞速版
                "25091RP04C", "25091RP04", "25091RP0",               // 小米平板8 Pro
                "25097RP43C", "25097RP4C", "25097RP4D",              // 小米平板8 Gen4
                "25079RPDCC", "25079RPDC",                           // 红米 K Pad
                "23046RP50C", "25046RP50C", "25046RP5"               // 小米平板6 Pro
            )
            if (enabling && !supported) {
                latencyMsg = "未适配此机型，不开启"
                return@LaunchedEffect
            }
            latencyMsg = "处理中…"
            scope2.launch {
                val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        fun su(c: String): String {
                            val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
                            val o = pr.inputStream.bufferedReader().readText()
                            pr.waitFor()
                            return o
                        }
                        if (enabling) {
                            // 先把脚本写到 /sdcard（App 有权限），再 cp 过去（短命令，不会超长）
                            // 写 App 私有目录（无需存储权限），再 cp 到 /data/adb
                            val scriptFile = java.io.File(ctx3.filesDir, "xy_game_daemon.sh")
                            try {
                                scriptFile.parentFile?.mkdirs()
                                scriptFile.writeText(perfGameScript.joinToString("\n"))
                            } catch (t: Throwable) { }
                            val sp = scriptFile.absolutePath.replace("'", "'\\''")
                            val r1 = su("rm -f /data/adb/xy_game.off; cat '" + sp + "' > /data/adb/xy_perf_daemon; chmod 755 /data/adb/xy_perf_daemon; ls -l /data/adb/xy_perf_daemon 2>/dev/null | awk '{print \$5}'")
                            // 开启：直接启动（脚本内部会单例处理，并自动识别机型+写第6行+配置）
                            su("for p in \$(ps -A -o PID,ARGS 2>/dev/null | grep -E 'xy_perf_daemon' | grep -v grep | grep -v 'sh -c' | awk '{print \$1}'); do kill -9 \$p 2>/dev/null; done; sleep 0.2; setsid sh /data/adb/xy_perf_daemon >/dev/null 2>&1 &")
                            "已开启"
                        } else {
                            // ===== 1. 按机型精确恢复（脚本记录的原始快照）=====
                            val sf2 = java.io.File(ctx3.filesDir, "xy_game_daemon.sh")
                            try { sf2.writeText(perfGameScript.joinToString("\n")) } catch (t: Throwable) { }
                            val sp2 = sf2.absolutePath.replace("'", "'\\''")
                            su("cat '" + sp2 + "' > /data/adb/xy_perf_daemon; chmod 755 /data/adb/xy_perf_daemon")
                            su("sh /data/adb/xy_perf_daemon restore >/dev/null 2>&1")
                            // ===== 2. 杀所有旧进程（任何名字）+ 删所有旧版本自启 =====
                            su("for p in \$(ps -A -o PID,ARGS 2>/dev/null | grep -E '性能模式|xy_perf_daemon|xy_game|xy_guard|xy_perf|perf_bind|jisu_mode' | grep -v grep | grep -v 'sh -c' | grep -v '恢复原样' | awk '{print \$1}'); do kill -9 \$p 2>/dev/null; done; sleep 0.3")
                            // 删所有"我们的"自启（只匹配 xy_/xiaoyu/性能/小鱼儿，绝不碰别人的）
                            su("rm -f /data/adb/service.d/xy_* /data/adb/service.d/*xiaoyu* /data/adb/service.d/*性能* /data/adb/service.d/*小鱼儿* 2>/dev/null")
                            su("rm -f /data/adb/post-fs-data.d/xy_* /data/adb/post-fs-data.d/*xiaoyu* /data/adb/post-fs-data.d/*性能* 2>/dev/null")
                            su("rm -f /data/adb/init.d/xy_* /data/adb/init.d/*xiaoyu* /data/adb/init.d/*性能* 2>/dev/null")
                            su("rm -f /data/adb/service.d/xy_game.sh /data/adb/service.d/xy_guard.sh /data/adb/service.d/xy_perf.sh /data/adb/service.d/xy_open_access.sh 2>/dev/null")
                            su("rm -f /data/adb/xy_perf_daemon /data/adb/xy_perf_daemon /data/adb/xy_game.sh 2>/dev/null")
                            // 1. 解禁所有（不管谁禁的，全部恢复）
                            su("pm list packages 2>/dev/null | sed 's/^package://' | while read pk; do pm unhide \"\$pk\" >/dev/null 2>&1; pm enable --user 0 \"\$pk\" >/dev/null 2>&1; done")
                            // 1.5 清理旧版本性能模式残留（小鱼儿1.7 / perf_ultimate / jisu_mode 等）
                            //     ★ 保护 /storage/emulated/0/小鱼儿 目录
                            su("rm -rf /data/adb/perf_ultimate /data/adb/perf_bind /data/adb/jisu_mode /sdcard/小鱼儿1.7 /sdcard/小鱼儿性能模式.sh /sdcard/小鱼儿性能.sh /data/local/tmp/jisu_mode 2>/dev/null")
                            // 1.6 恢复 GED boost / force / DDR / 温控模拟温度
                            su("for k in boost_gpu_enable ged_smart_boost ged_boost_enable; do [ -f /sys/module/ged/parameters/\$k ] && echo 0 > /sys/module/ged/parameters/\$k 2>/dev/null; done")
                            su("for k in force_clk_on force_bus_on force_rail_on; do [ -f /sys/class/kgsl/kgsl-3d0/\$k ] && echo 0 > /sys/class/kgsl/kgsl-3d0/\$k 2>/dev/null; done")
                            su("for L in /sys/devices/system/cpu/bus_dcvs/DDR /sys/devices/system/cpu/bus_dcvs/DDRQOS /sys/devices/system/cpu/bus_dcvs/LLCC /sys/devices/system/cpu/bus_dcvs/L3; do [ -d \"\$L\" ] && { cat \"\$L/hw_min_freq\" 2>/dev/null > \"\$L/boost_freq\" 2>/dev/null; }; done")
                            su("for z in /sys/class/thermal/thermal_zone*/; do [ -d \"\$z\" ] && echo 0 > \"\$z/emul_temp\" 2>/dev/null; done")
                            // 1.7 恢复新增优化项（KSM/L3/DDR/sched_boost）
                            su("echo 1 > /sys/kernel/mm/ksm/run 2>/dev/null; echo 0 > /proc/sys/walt/sched_boost 2>/dev/null; echo 0 > /proc/sys/kernel/sched_boost 2>/dev/null; echo 0 > /proc/sys/kernel/softlockup_panic 2>/dev/null")
                            su("for L in /sys/devices/system/cpu/bus_dcvs/L3 /sys/devices/system/cpu/bus_dcvs/DDR; do [ -d \"\$L\" ] && { cat \"\$L/hw_min_freq\" 2>/dev/null > \"\$L/boost_freq\" 2>/dev/null; }; done")
                            // 2. 解除所有 bind 遮罩
                            su("for m in /proc/powerhal_cpu_ctrl/perfserv_freq /proc/powerhal_cpu_ctrl/adpf_enable /proc/perfmgr/perf_ioctl /sys/module/perfmgr/parameters/min_freq_limit_level /sys/module/perfmgr/parameters/max_freq_limit_level /sys/module/perfmgr/parameters/gpu_req_min_freq /proc/thermal_message/temp_state /proc/thermal_message/market_download_limit /proc/thermal_message/cpu_nolimit_temp; do while umount \$m 2>/dev/null; do :; done; done")
                            su("for m in /system/product/etc/displayconfig/*thermal*.xml /system/vendor/etc/thermal/*thermal*fps*.xml; do [ -e \"\$m\" ] && { while umount \"\$m\" 2>/dev/null; do :; done; }; done; rm -f /data/local/tmp/xy_empty")
                            su("rm -rf /dev/xy_ps_bak /dev/xy_pmi_empty /dev/xy_tm_* /dev/scene_fast* /dev/scene_sd_* /dev/scene_* 2>/dev/null")
                            // 3. 恢复 CPU 频率 + governor
                            su("for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do hw=\$(cat \$p/cpuinfo_max_freq 2>/dev/null); mn=\$(cat \$p/cpuinfo_min_freq 2>/dev/null); [ -n \"\$hw\" ] && { chmod 0666 \$p/scaling_max_freq \$p/scaling_min_freq \$p/scaling_governor 2>/dev/null; echo \$hw > \$p/scaling_max_freq 2>/dev/null; [ -n \"\$mn\" ] && echo \$mn > \$p/scaling_min_freq 2>/dev/null; av=\$(cat \$p/scaling_available_governors 2>/dev/null); case \"\$av\" in *sugov_ext*) echo sugov_ext > \$p/scaling_governor 2>/dev/null;; *walt*) echo walt > \$p/scaling_governor 2>/dev/null;; *schedutil*) echo schedutil > \$p/scaling_governor 2>/dev/null;; esac; }; done")
                            // 4. 恢复温控 + emul_temp
                            su("for z in /sys/class/thermal/thermal_zone*/mode; do [ -f \$z ] && echo enabled > \$z 2>/dev/null; done; for z in /sys/class/thermal/thermal_zone*/emul_temp; do [ -f \$z ] && echo 0 > \$z 2>/dev/null; done")
                            // 5. 恢复 perfserv/adpf
                            su("[ -f /proc/powerhal_cpu_ctrl/perfserv_freq ] && echo 0 > /proc/powerhal_cpu_ctrl/perfserv_freq 2>/dev/null; [ -f /proc/powerhal_cpu_ctrl/adpf_enable ] && echo 1 > /proc/powerhal_cpu_ctrl/adpf_enable 2>/dev/null; [ -f /proc/powerhal_cpu_ctrl/sf_hint_low_power_enabled ] && echo 1 > /proc/powerhal_cpu_ctrl/sf_hint_low_power_enabled 2>/dev/null")
                            // 6. 恢复 GPU（MTK -1 / 骁龙 mod 125）+ GED
                            su("for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do [ -f \$f ] && echo -1 > \$f 2>/dev/null; done; [ -f /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent ] && echo 125 > /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 2>/dev/null; [ -f /sys/class/kgsl/kgsl-3d0/devfreq/governor ] && echo msm-adreno-tz > /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null; [ -f /sys/module/ged/parameters/gpu_cust_upbound_freq ] && echo 1300000 > /sys/module/ged/parameters/gpu_cust_upbound_freq 2>/dev/null")
                            // 7. 恢复 core_ctl + cpuset
                            su("for d in /sys/devices/system/cpu/cpu*/core_ctl; do [ -d \"\$d\" ] && echo 1 > \$d/enable 2>/dev/null; done")
                            su("NP=\$(ls -d /sys/devices/system/cpu/cpu[0-9]* 2>/dev/null | wc -l); [ \"\$NP\" -gt 0 ] 2>/dev/null || NP=8; for d in background system-background foreground top-app; do [ -d /dev/cpuset/\$d ] && echo \"0-\$((NP-1))\" > /dev/cpuset/\$d/cpus 2>/dev/null; done")
                            // 8. 恢复 walt / uclamp / sched_boost / perfmgr
                            su("[ -f /proc/sys/walt/sched_boost ] && echo 0 > /proc/sys/walt/sched_boost 2>/dev/null; [ -f /proc/sys/kernel/sched_util_clamp_min ] && echo 0 > /proc/sys/kernel/sched_util_clamp_min 2>/dev/null; [ -f /proc/sys/kernel/sched_util_clamp_max ] && echo 1024 > /proc/sys/kernel/sched_util_clamp_max 2>/dev/null; [ -f /sys/module/perfmgr/parameters/perfmgr_enable ] && echo 1 > /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null")
                            // 9. 删刷新率设置
                            su("settings delete system peak_refresh_rate >/dev/null 2>&1; settings delete system min_refresh_rate >/dev/null 2>&1; settings delete system user_refresh_rate >/dev/null 2>&1; settings delete secure user_refresh_rate >/dev/null 2>&1")
                            // 10. 删所有我们创建的文件（新+老版本）
                            su("rm -f /data/adb/xy_game.off /data/adb/xy_game.sh /data/adb/xy_game.pid /data/adb/xy_check.sh /data/adb/xy_open_access.sh /data/adb/xy_freq_lock /data/adb/xy_maxf /data/adb/xy_orig_governor /data/adb/xy_perf_status /data/adb/xy_thermal_bak /data/adb/xy_thermal_zones /data/adb/xy_cur_app /data/adb/xy_game_pkgs_disabled /data/adb/xy_sleep_ts /data/adb/xy_ts_* /data/adb/xy_ut_* 2>/dev/null")
                            su("rm -f /data/adb/service.d/xy_game.sh /data/adb/service.d/xy_guard.sh /data/local/tmp/xy_* /data/local/tmp/.rt_check /data/local/tmp/.xy_mon* 2>/dev/null")
                            su("rm -rf /data/adb/xy_thermal_cfg_bak /dev/scene_fast /dev/scene_fast_* 2>/dev/null")
                            // 11. 恢复小米温控配置（如果之前备份过）
                            su("if [ -d /data/adb/xy_thermal_cfg_bak ] && [ -d /data/vendor/thermal/config ]; then cp -rf /data/adb/xy_thermal_cfg_bak/. /data/vendor/thermal/config/ 2>/dev/null; fi")
                            "已关闭（全部恢复 + 清理残留）"
                        }
                    } catch (t: Throwable) {
                        "操作失败"
                    }
                }
                latencyMsg = msg
            }
        }
        if (title == "执行此文件") {
            // 点击时也刷新一次（保险）
            execPath = withContext(kotlinx.coroutines.Dispatchers.IO) { loadExecPath(slot) }
        }
        if (title == "隐藏BL") {
            on = ctx3.getSharedPreferences("yx_mode", 0).getBoolean("bl", false) ||
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    fun q(c: String): String {
                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
            // 检测自己的文件是否存在（存在=开，不存在=关）
            val exists = withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                        "[ -f /data/adb/service.d/xy_open_access.sh ] && echo 1 || echo 0"))
                    val o = pr.inputStream.bufferedReader().readText().trim(); pr.waitFor()
                    o == "1"
                } catch (t: Throwable) { false }
            }
            if (on && exists) {
                // 关闭：删文件
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    runCatching {
                        Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                            "rm -f /data/adb/service.d/xy_open_access.sh")).waitFor()
                    }
                }
                latencyMsg = "已关闭"
            } else if (on && !exists) {
                latencyMsg = "放开通行未部署"
            } else {
                latencyMsg = "已关闭"
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
                clickTick++
                if (title == "刷入内核模块") {
                    latencyMsg = "导出中…"
                    scope2.launch {
                        val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun sh(c: String): String {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
                    } else if (path.endsWith(".zip")) {
                        // zip = 模块 → ksud 安装（不是 sh 执行）
                        val qz = "'" + path.replace("'", "'\''") + "'"
                        val cmd = "ZID=$(unzip -p " + qz + " module.prop 2>/dev/null | grep '^id=' | cut -d= -f2 | tr -d '\r') ; " +
                            "[ -n \"\$ZID\" ] && { rm -rf \"/data/adb/modules/\$ZID\" \"/data/adb/modules_update/\$ZID\" ; } ; " +
                            "export HOME=/data/local/tmp ; export TMPDIR=/data/local/tmp ; " +
                            "\"/data/adb/ksud\" module install " + qz + " 2>&1 ; " +
                            "if [ -f \"/data/adb/modules/\$ZID/module.prop\" ] || [ -f \"/data/adb/modules_update/\$ZID/module.prop\" ]; then echo \"[OK] \$ZID installed\" ; else echo \"[FAIL]\" ; fi"
                        latencyMsg = "安装模块…"
                        scope2.launch {
                            withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", cmd))
                                    val o = pr.inputStream.bufferedReader().readText()
                                    pr.waitFor()
                                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                                        latencyMsg = o.take(200).replace("\n", " ")
                                    }
                                } catch (t: Throwable) {
                                    withContext(kotlinx.coroutines.Dispatchers.Main) { latencyMsg = "安装失败: " + (t.message ?: "") }
                                }
                            }
                        }
                    } else if (path.endsWith(".so")) {
                        keyInput = ""
                        showKeyDialog = true
                    } else {
                        latencyMsg = "执行中…"
                        scope2.launch {
                            val msg = withContext(kotlinx.coroutines.Dispatchers.IO) {
                                try {
                                    fun su2(c: String): String {
                                        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
                                    val p = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
                if (title == "放开通行") {
                    val enabling = on
                    scope2.launch {
                        withContext(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                fun su(c: String): String {
                                    val p = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", c))
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
                    ?: (if (title == "执行此文件") (execPath ?: "暂无设置") else ""),
                color = Color(0x66FFFFFF),
                fontSize = 9.sp,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .then(
                        if (title == "执行此文件") {
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
                                        suBin(), "--mount-master", "-c",
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
                                Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c", cmd)).waitFor()
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
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
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
            arrayOf(suBin(), "--mount-master", "-c", "cp " + shq(src) + " " + shq(dest) + " && chmod 644 " + shq(dest))
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
        val pr = ProcessBuilder(suBin(), "--mount-master", "-c", cmd).redirectErrorStream(true).start()
        val done = java.util.concurrent.Executors.newSingleThreadExecutor().submit<Boolean> {
            pr.waitFor(); true
        }.get(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
        val out = pr.inputStream.bufferedReader().readText()
        if (done) out else ""
    } catch (t: Throwable) {
        ""
    }
}

// ===== 性能模式脚本（v13，机型识别 + 按机型配置）=====
private val perfGameScript = listOf(
    "#!/system/bin/sh",
    "# ============================================================",
    "#  极道帝兵 · 性能模式 v13（机型识别 + 前台满配）",
    "#  依据：Scene 8.3.0 极速模式 前台实测",
    "#  注意：不写 service.d 自启（防 bootloop）",
    "# ============================================================",
    "ACTION=\"\$1\"",
    "SNAP=/data/adb/xy_orig_state",
    "[ \"\$ACTION\" != \"restore\" ] && [ -f /data/adb/xy_game.off ] && exit 0",
    "PIDFILE=/data/adb/xy_game.pid",
    "# 严格单例：先按 PIDFILE 杀旧的，再扫一遍所有 xy_game.sh 进程",
    "if [ -f \"\$PIDFILE\" ]; then",
    "  _old=\$(cat \"\$PIDFILE\" 2>/dev/null)",
    "  [ -n \"\$_old\" ] && [ \"\$_old\" != \"\$\$\" ] && kill -9 \"\$_old\" 2>/dev/null",
    "fi",
    "for old in \$(ps -A -o PID,ARGS 2>/dev/null | grep -E '性能模式|xy_perf_daemon|xy_game\\.sh' | grep -v grep | grep -v 'sh -c' | awk '{print \$1}'); do",
    "  [ \"\$old\" != \"\$\$\" ] && [ \"\$old\" != \"\$PPID\" ] && kill -9 \"\$old\" 2>/dev/null",
    "done",
    "sleep 0.3",
    "echo \$\$ > \"\$PIDFILE\"",
    "",
    "N=\$(ls -d /sys/devices/system/cpu/cpu[0-9]* 2>/dev/null | wc -l); [ \"\$N\" -gt 0 ] 2>/dev/null || N=8",
    "",
    "# ============ 一、识别 ============",
    "MODEL=\$(getprop ro.product.model 2>/dev/null)",
    "BRAND=\$(getprop ro.product.brand 2>/dev/null)",
    "PLAT=\$(getprop ro.board.platform 2>/dev/null)",
    "HW=\$(getprop ro.hardware 2>/dev/null)",
    "SOCKEY=\"\$PLAT\$HW\"",
    "",
    "PROFILE=\"\"",
    "case \"\$MODEL\" in",
    "  25091RP04C|25091RP04|25091RP0) PROFILE=\"mi_pad8pro\" ;;   # 小米平板8 Pro（骁龙8Elite，实测）",
    "  25097RP43C|25097RP4C|25097RP4D) PROFILE=\"mi_pad8gen4\" ;;  # 小米平板8（骁龙8Gen4，实测）",
    "  23046RP50C|25046RP50C|25046RP5) PROFILE=\"mi_pad6pro\" ;; # 小米平板6 Pro（骁龙8Gen1，实测）",
    "  25079RPDCC|25079RPDC)          PROFILE=\"redmi_kpad\" ;;   # 红米K Pad（天玑9400+，实测）",
    "  PLF110|PKG110)                 PROFILE=\"op_ace5\" ;;      # 一加Ace5 竞速版（天玑9400，实测）",
    "esac",
    "if [ -z \"\$PROFILE\" ]; then",
    "  case \"\$SOCKEY\" in",
    "    *mt6989*|*MT6989*) PROFILE=\"op_ace5\" ;;      # 同芯片（天玑9400）复用",
    "    *sun*)             PROFILE=\"mi_pad8pro\" ;;   # 同芯片（骁龙8Elite）复用",
    "  esac",
    "fi",
    "if [ -z \"\$PROFILE\" ]; then",
    "  if   [ -f /proc/powerhal_cpu_ctrl/perfserv_freq ]; then PROFILE=\"mtk_generic\"",
    "  elif [ -f /sys/class/kgsl/kgsl-3d0/devfreq/governor ]; then PROFILE=\"sd_generic\"",
    "  else PROFILE=\"generic\"; fi",
    "fi",
    "",
    "# ============ 二、配置表（只做前台满配）============",
    "# P_MAXPCT : 每簇上限百分比，逗号分隔（policy0,4,7…）缺省 100",
    "# P_MIN    : hw_min | 数字百分比 | max",
    "# P_GOV    : sugov_ext | walt | auto",
    "# P_TL     : keep（不动系统默认）| 2/3/4/5（按频率比例生成）",
    "# P_RATE   : up/down rate 值（10 = 极致跟手；keep = 不动）",
    "# P_CPSET  : all | 0-3,0-5,0-5,0-7（bg,sysbg,fg,top 四段）",
    "# P_GPU    : disabled | mod100 | max",
    "# P_GPOUP  : GED gpu_cust_upbound_freq（0=不动）",
    "# P_PS     : clear（proc 挂载遮罩）| keep",
    "# P_THM    : shell（只关 shell_front/frame/back）| both | hard | off",
    "# P_CORE   : 每簇 \"min/max/enable\" 逗号分隔（0,4,7 顺序）",
    "# P_UCLAMP : 1024 | 0 | keep",
    "# P_TAP    : 1 = GPU THERMAL_AP 限制设 -1",
    "# P_HZ     : max（拉满）| keep",
    "# P_BIND   : 1 | 0",
    "profile_load() {",
    "  # 扩展字段默认值（空=不用）",
    "  P_MAXABS=\"\"; P_MINABS=\"\"; P_HIS=\"\"; P_TLSTR=\"\"; P_UP=\"\"; P_DOWN=\"\"",
    "  P_GPUMX=\"0\"; P_GPUMN=\"\"; P_CORE_CPU=\"\"; P_TMSG=\"0\"; P_PKGSET=\"\"",
    "  case \"\$PROFILE\" in",
    "    op_ace5|mtk_d9400)   # 一加Ace5 天玑9400（全拉满：max=hw，GED 不限）",
    "      P_MAXPCT=\"100,100,100\"; P_MAXABS=\"\"; P_MIN=\"hw_min\"; P_GOV=\"performance\"; P_TL=\"keep\"; P_UP=\"10\"; P_DOWN=\"5000\"",
    "      P_CPSET=\"0-3,0-5,0-5,0-7\"; P_GPU=\"disabled\"; P_GPOUP=\"1066000\"; P_PS=\"clear\"",
    "      P_THM=\"shell\"; P_CORE=\"4/4/1,3/3/1,1/1/1\"; P_UCLAMP=\"1024\"; P_TAP=\"1\"",
    "      P_HZ=\"120\"; P_BIND=\"1\"; P_PKGS=\"1\"; P_PKGSET=\"green\" ;;",
    "    mi_pad8pro|sd_8elite)    # 小米平板8 Pro 骁龙8Elite（全拉满版：max=hw，GPU 不限）",
    "      P_MAXPCT=\"100,100,100,100\"; P_MAXABS=\"3532800,4320000,4320000,4320000\"; P_MINABS=\"\"",
    "      P_HIS=\"1152000,1401600\"",
    "      P_TLSTR=\"75 1689600:80 2400000:90|75 2246400:80 2841600:85 3513600:90\"",
    "      P_GOV=\"performance\"; P_UP=\"2000\"; P_DOWN=\"5000\"; P_MIN=\"max\"",
    "      P_CPSET=\"0-3,0-5,0-7,0-7\"; P_GPU=\"mod125\"; P_GPUMX=\"0\"; P_PS=\"keep\"",
    "      P_THM=\"tmsg\"; P_CORE_CPU=\"6:6/6/1\"; P_UCLAMP=\"1024\"; P_TAP=\"0\"; P_WALT=\"1\"",
    "      P_HZ=\"144\"; P_BIND=\"1\"; P_PKGS=\"1\"; P_PKGSET=\"mi\" ;;",
    "    redmi_kpad|mtk_d9400p)   # 红米K Pad 天玑9400+（全拉满）",
    "      P_MAXPCT=\"100,100,100\"; P_MAXABS=\"\"; P_MIN=\"hw_min\"",
    "      P_GOV=\"performance\"; P_TL=\"keep\"; P_UP=\"10\"; P_DOWN=\"5000\"",
    "      P_CPSET=\"0-3,0-5,0-5,0-7\"; P_GPU=\"disabled\"; P_GPOUP=\"1612000\"; P_PS=\"clear\"",
    "      P_THM=\"shell\"; P_CORE=\"4/4/1,3/3/1,1/1/1\"; P_UCLAMP=\"1024\"; P_TAP=\"1\"",
    "      P_HZ=\"max\"; P_BIND=\"1\"; P_PKGSET=\"mi\" ;;",
    "    mi_pad6pro|sd_8gen1)     # 小米平板6 Pro 骁龙8Gen1（23046RP50C 实测 Scene 极速）",
    "      P_MAXPCT=\"100,100,100\"; P_MAXABS=\"\"",
    "      P_MIN=\"max\"",
    "      P_HIS=\"1228800,1555200,1651200\"",
    "      P_TLSTR=\"10 1664837:-6|10 2569875:0|10 2587265:0 2703984:-6\"",
    "      P_GOV=\"performance\"; P_UP=\"0\"; P_DOWN=\"1000\"",
    "      P_CPSET=\"0-3,0-3,0-5,0-7\"; P_GPU=\"mod125\"; P_GPUMX=\"0\"; P_PS=\"keep\"",
    "      P_THM=\"tmsg\"; P_CORE_CPU=\"0:4/4/1,4:3/3/1,7:1/1/1\"; P_UCLAMP=\"1024\"",
    "      P_HZ=\"max\"; P_BIND=\"1\"; P_PKGSET=\"mi\" ;;",
    "    mi_pad8gen4|sd_8gen4)    # 小米平板8 骁龙8Gen4（全拉满：max=hw/perf/GPU温控解除）",
    "      P_MAXPCT=\"100,100,100,100\"; P_MAXABS=\"\"",
    "      P_MIN=\"max\"",
    "      P_HIS=\"960000,1190400,1190400,1478400\"",
    "      P_TLSTR=\"-6 1594376:-13 1916794:-19|-6 1793780:-13 2271414:-19|0|0\"",
    "      P_GOV=\"performance\"; P_RATE=\"10\"",
    "      P_CPSET=\"0-3,0-5,0-7,0-7\"; P_GPU=\"mod125\"; P_GPUMX=\"0\"; P_GPUMN=\"0\"; P_PS=\"keep\"",
    "      P_THM=\"tmsg\"; P_CORE_CPU=\"0:1/2/1,2:3/3/1,5:2/2/1,7:1/1/1\"; P_UCLAMP=\"1024\"; P_TAP=\"1\"; P_WALT=\"1\"",
    "      P_HZ=\"120\"; P_BIND=\"1\"; P_PKGSET=\"mi\" ;;",
    "    *)                       # 未知设备：最保守",
    "      P_MAXPCT=\"100\"; P_MIN=\"hw_min\"; P_GOV=\"auto\"; P_TL=\"keep\"; P_RATE=\"keep\"",
    "      P_CPSET=\"all\"; P_GPU=\"disabled\"; P_GPOUP=\"0\"; P_PS=\"keep\"",
    "      P_THM=\"off\"; P_CORE=\"auto\"; P_UCLAMP=\"keep\"; P_TAP=\"0\"",
    "      P_HZ=\"keep\"; P_BIND=\"1\" ;;",
    "  esac",
    "}",
    "# 机型名称 → 写进 /storage/emulated/0/小鱼儿/小鱼儿密钥 第 6 行（只覆盖第 6 行，前 5 行保留）",
    "KEYF=/storage/emulated/0/小鱼儿/小鱼儿密钥",
    "case \"\$PROFILE\" in",
    "  op_ace5)     NAME=\"一加 Ace5 竞速版\" ;;",
    "  mi_pad8pro)  NAME=\"小米平板8 Pro\" ;;",
    "  mi_pad8gen4) NAME=\"小米平板8\" ;;",
    "  redmi_kpad)  NAME=\"红米 K Pad\" ;;",
    "  mi_pad6pro)  NAME=\"小米平板6 Pro\" ;;",
    "  *)           NAME=\"未适配此机型\" ;;",
    "esac",
    "mkdir -p /storage/emulated/0/小鱼儿 2>/dev/null",
    "if [ -f \"\$KEYF\" ]; then",
    "  awk -v n=6 -v v=\"\$NAME\" 'NR==n{print v; f=1; next} {print} END{if(!f) for(i=NR+1;i<=n;i++) print (i==n?v:\"\")}' \"\$KEYF\" > /data/local/tmp/.key6 2>/dev/null && cat /data/local/tmp/.key6 > \"\$KEYF\" 2>/dev/null && rm -f /data/local/tmp/.key6",
    "else",
    "  printf '\\n\\n\\n\\n\\n%s\\n' \"\$NAME\" > \"\$KEYF\" 2>/dev/null",
    "fi",
    "chmod 600 \"\$KEYF\" 2>/dev/null",
    "",
    "# 只支持已实测的机型：识别不到就不开启",
    "case \"\$PROFILE\" in",
    "  op_ace5|mi_pad8pro|mi_pad8gen4|redmi_kpad|mi_pad6pro) : ;;",
    "  *)",
    "    echo \"  [未适配] 机型 \$MODEL（\$SOCKEY）没有配置，不开启性能模式\"",
    "    echo \"  已写入第6行: \$NAME\"",
    "    # 删掉自己的守护文件，避免 App 误判为\"已开启\"",
    "    rm -f /data/adb/xy_perf_daemon /data/adb/xy_game.pid 2>/dev/null",
    "    exit 0 ;;",
    "esac",
    "",
    "profile_load",
    "echo \"[profile] \$PROFILE (\$MODEL / \$SOCKEY)\"",
    "",
    "# ============ 三、工具 ============",
    "W() { [ -f \"\$1\" ] && { chmod 0666 \"\$1\" 2>/dev/null; echo \"\$2\" > \"\$1\" 2>/dev/null; }; }",
    "",
    "pct_at() {  # \$1=逗号列表 \$2=索引(0起) → 百分比（缺省100）",
    "  echo \"\$1\" | awk -F, -v i=\"\$2\" '{print (i+1<=NF)? \$((i+1)) : 100}'",
    "}",
    "",
    "# ============ 四、应用 ============",
    "apply_cpu() {",
    "  : > /data/adb/xy_freq_lock 2>/dev/null",
    "  idx=0",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    mn=\$(cat \"\$p/cpuinfo_min_freq\" 2>/dev/null)",
    "    pct=\$(echo \"\$P_MAXPCT\" | cut -d, -f\$((idx+1)))",
    "    [ -z \"\$pct\" ] && pct=100",
    "    [ \"\$pct\" = \"\$P_MAXPCT\" ] && pct=100",
    "    case \"\$pct\" in *[!0-9]*) pct=100 ;; esac",
    "    want_max=\$((hw * pct / 100))",
    "    # 绝对上限覆盖（如 ,,3250000 或 2745600,4089600）",
    "    abs=\$(echo \"\$P_MAXABS\" | cut -d, -f\$((idx+1)) 2>/dev/null)",
    "    case \"\$abs\" in ''|*[!0-9]*) : ;; *) [ \"\$abs\" -gt 0 ] 2>/dev/null && want_max=\$abs ;; esac",
    "    echo \"\$(basename \$p) \$want_max\" >> /data/adb/xy_freq_lock 2>/dev/null",
    "    chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" \"\$p/scaling_governor\" 2>/dev/null",
    "    case \"\$P_GOV\" in",
    "      sugov_ext) W \"\$p/scaling_governor\" sugov_ext ;;",
    "      walt)      W \"\$p/scaling_governor\" walt ;;",
    "      performance) W \"\$p/scaling_governor\" performance ;;",
    "      auto)",
    "        av=\$(cat \"\$p/scaling_available_governors\" 2>/dev/null)",
    "        case \"\$av\" in *sugov_ext*) W \"\$p/scaling_governor\" sugov_ext ;; *walt*) W \"\$p/scaling_governor\" walt ;; *schedutil*) W \"\$p/scaling_governor\" schedutil ;; esac ;;",
    "    esac",
    "    W \"\$p/scaling_max_freq\" \"\$want_max\"",
    "    minabs=\$(echo \"\$P_MINABS\" | cut -d, -f\$((idx+1)) 2>/dev/null)",
    "    case \"\$minabs\" in",
    "      ''|*[!0-9]*) minabs=\"\" ;;",
    "    esac",
    "    if [ -n \"\$minabs\" ]; then",
    "      W \"\$p/scaling_min_freq\" \"\$minabs\"",
    "    else",
    "      case \"\$P_MIN\" in",
    "        hw_min) [ -n \"\$mn\" ] && W \"\$p/scaling_min_freq\" \"\$mn\" ;;",
    "        max)",
    "          # 读实际 max（可能被 P_MAXABS/热压改过），避免 min > max",
    "          _mx=\$(cat \"\$p/scaling_max_freq\" 2>/dev/null)",
    "          [ -n \"\$_mx\" ] && W \"\$p/scaling_min_freq\" \"\$_mx\"",
    "          ;;",
    "        *) W \"\$p/scaling_min_freq\" \$((hw * P_MIN / 100)) ;;",
    "      esac",
    "    fi",
    "    # hispeed（骁龙 walt）",
    "    his=\$(echo \"\$P_HIS\" | cut -d, -f\$((idx+1)) 2>/dev/null)",
    "    case \"\$his\" in ''|*[!0-9]*) : ;; *) W \"\$p/walt/hispeed_freq\" \"\$his\"; W \"\$p/sugov_ext/hispeed_freq\" \"\$his\" ;; esac",
    "    idx=\$((idx+1))",
    "  done",
    "  # 调频阻尼（P_UP/P_DOWN 优先，其次 P_RATE）",
    "  RUP=\"\$P_UP\"; RDN=\"\$P_DOWN\"",
    "  [ -z \"\$RUP\" ] && [ \"\$P_RATE\" != \"keep\" ] && { RUP=\"\$P_RATE\"; RDN=\"\$P_RATE\"; }",
    "  [ -n \"\$RUP\" ] && {",
    "    for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "      [ -d \"\$p\" ] || continue",
    "      for sub in sugov_ext walt; do",
    "        W \"\$p/\$sub/up_rate_limit_us\" \"\$RUP\"",
    "        [ -n \"\$RDN\" ] && W \"\$p/\$sub/down_rate_limit_us\" \"\$RDN\"",
    "      done",
    "    done",
    "  }",
    "}",
    "",
    "apply_tl() {",
    "  if [ -n \"\$P_TLSTR\" ]; then",
    "    idx=0",
    "    for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "      [ -d \"\$p\" ] || continue",
    "      if   [ -f \"\$p/walt/target_loads\" ]; then f=\"\$p/walt/target_loads\"",
    "      elif [ -f \"\$p/sugov_ext/target_loads\" ]; then f=\"\$p/sugov_ext/target_loads\"; else idx=\$((idx+1)); continue; fi",
    "      TL=\$(echo \"\$P_TLSTR\" | cut -d'|' -f\$((idx+1)))",
    "      case \"\$TL\" in ''|*[!0-9]*:) : ;; *) W \"\$f\" \"\$TL\"; [ \"\$(cat \"\$f\" 2>/dev/null)\" = \"\$TL\" ] || W \"\$f\" \"\$TL\" ;; esac",
    "      idx=\$((idx+1))",
    "    done",
    "    return 0",
    "  fi",
    "  [ \"\$P_TL\" = \"keep\" ] && return 0",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    if   [ -f \"\$p/sugov_ext/target_loads\" ]; then f=\"\$p/sugov_ext/target_loads\"",
    "    elif [ -f \"\$p/walt/target_loads\" ]; then f=\"\$p/walt/target_loads\"; else continue; fi",
    "    case \"\$P_TL\" in",
    "      5) TL=\"80 \$((hw*38/100)):85 \$((hw*56/100)):90 \$((hw*76/100)):95 \$((hw*88/100)):100\" ;;",
    "      4) TL=\"78 \$((hw*45/100)):83 \$((hw*62/100)):88 \$((hw*80/100)):93\" ;;",
    "      3) TL=\"75 \$((hw*55/100)):80 \$((hw*70/100)):85 \$((hw*86/100)):90\" ;;",
    "      2) TL=\"75 \$((hw*58/100)):80 \$((hw*82/100)):90\" ;;",
    "      *) TL=\"80\" ;;",
    "    esac",
    "    W \"\$f\" \"\$TL\"; [ \"\$(cat \"\$f\" 2>/dev/null)\" = \"\$TL\" ] || W \"\$f\" \"\$TL\"",
    "  done",
    "}",
    "",
    "apply_cpuset() {",
    "  case \"\$P_CPSET\" in",
    "    all) SETS=\"0-\$((N-1)),0-\$((N-1)),0-\$((N-1)),0-\$((N-1))\" ;;",
    "    *)   SETS=\"\$P_CPSET\" ;;",
    "  esac",
    "  i=0",
    "  for d in background system-background foreground top-app; do",
    "    [ -d \"/dev/cpuset/\$d\" ] || { i=\$((i+1)); continue; }",
    "    v=\$(echo \"\$SETS\" | cut -d, -f\$((i+1)))",
    "    [ -n \"\$v\" ] && W \"/dev/cpuset/\$d/cpus\" \"\$v\"",
    "    i=\$((i+1))",
    "  done",
    "}",
    "",
    "apply_gpu() {",
    "  case \"\$P_GPU\" in",
    "    disabled) for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do W \"\$f\" -1; done",
    "              # GED boost 拉满（MTK GPU 提频）",
    "              W /sys/module/ged/parameters/boost_gpu_enable 1",
    "              W /sys/module/ged/parameters/ged_smart_boost 1",
    "              W /sys/module/ged/parameters/ged_boost_enable 1 ;;",
    "    mod100)   for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do W \"\$f\" -1; done",
    "              W /sys/class/kgsl/kgsl-3d0/devfreq/governor msm-adreno-tz",
    "              W /sys/class/kgsl/kgsl-3d0/min_pwrlevel 0",
    "              W /sys/class/kgsl/kgsl-3d0/max_pwrlevel 0",
    "              W /sys/class/kgsl/kgsl-3d0/force_clk_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/force_bus_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/force_rail_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 100",
    "              [ \"\$P_GPUMX\" != \"0\" ] && W /sys/class/kgsl/kgsl-3d0/devfreq/max_freq \"\$P_GPUMX\" ;;",
    "    mod125)   W /sys/class/kgsl/kgsl-3d0/devfreq/governor msm-adreno-tz",
    "              # 强制 GPU 满血（不降频/不掉电）",
    "              W /sys/class/kgsl/kgsl-3d0/min_pwrlevel 0",
    "              W /sys/class/kgsl/kgsl-3d0/max_pwrlevel 0",
    "              W /sys/class/kgsl/kgsl-3d0/force_clk_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/force_bus_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/force_rail_on 1",
    "              W /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 125",
    "              [ \"\$P_GPUMX\" != \"0\" ] && W /sys/class/kgsl/kgsl-3d0/devfreq/max_freq \"\$P_GPUMX\"",
    "              [ -n \"\$P_GPUMN\" ] && [ \"\$P_GPUMN\" != \"0\" ] && W /sys/class/kgsl/kgsl-3d0/devfreq/min_freq \"\$P_GPUMN\" ;;",
    "    mod125)   W /sys/class/kgsl/kgsl-3d0/devfreq/governor msm-adreno-tz",
    "              W /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 125",
    "              [ \"\$P_GPUMX\" != \"0\" ] && W /sys/class/kgsl/kgsl-3d0/devfreq/max_freq \"\$P_GPUMX\"",
    "              [ -n \"\$P_GPUMN\" ] && [ \"\$P_GPUMN\" != \"0\" ] && W /sys/class/kgsl/kgsl-3d0/devfreq/min_freq \"\$P_GPUMN\" ;;",
    "    max)      for f in /proc/gpufreqv2/fix_target_opp_index /proc/gpufreq/fix_target_opp_index; do W \"\$f\" -1; W \"\$f\" \"0 0\"; done ;;",
    "  esac",
    "  # GED（系统会自己改，守护里也维持）",
    "  [ \"\$P_GPOUP\" != \"0\" ] && W /sys/module/ged/parameters/gpu_cust_upbound_freq \"\$P_GPOUP\"",
    "  # GPU 温控限制解除（THERMAL_AP ceiling = -1）",
    "  if [ \"\$P_TAP\" = \"1\" ] && [ -f /proc/gpufreqv2/limit_table ]; then",
    "    tmp=\$(cat /proc/gpufreqv2/limit_table 2>/dev/null | awk 'BEGIN{OFS=\" \"} /THERMAL_AP/{\$4=\"-1\"} {print}' 2>/dev/null)",
    "    [ -n \"\$tmp\" ] && echo \"\$tmp\" > /proc/gpufreqv2/limit_table 2>/dev/null",
    "  fi",
    "}",
    "",
    "apply_ps_mask() {",
    "  f=/proc/powerhal_cpu_ctrl/perfserv_freq",
    "  [ -e \"\$f\" ] || return 0",
    "  # 内容保留原值（写 0 会把 p0 压到 1.5G）",
    "  if [ ! -s /dev/xy_ps_bak ]; then",
    "    while umount \"\$f\" 2>/dev/null; do :; done",
    "    cp -f \"\$f\" /dev/xy_ps_bak 2>/dev/null",
    "  fi",
    "  [ -s /dev/xy_ps_bak ] || return 0",
    "  # 先清掉所有旧层，再挂一层（避免叠加）",
    "  while umount \"\$f\" 2>/dev/null; do :; done",
    "  mount --bind /dev/xy_ps_bak \"\$f\" 2>/dev/null",
    "",
    "}",
    "apply_pmi_mask() {",
    "  f=/proc/perfmgr/perf_ioctl",
    "  [ -e \"\$f\" ] || return 0",
    "  : > /dev/xy_pmi_empty 2>/dev/null",
    "  while umount \"\$f\" 2>/dev/null; do :; done",
    "  mount --bind /dev/xy_pmi_empty \"\$f\" 2>/dev/null",
    "",
    "}",
    "apply_perfserv() {",
    "  [ \"\$P_PS\" = \"clear\" ] || return 0",
    "  apply_ps_mask",
    "  apply_pmi_mask",
    "  W /proc/powerhal_cpu_ctrl/adpf_enable 0",
    "  W /proc/powerhal_cpu_ctrl/sf_hint_low_power_enabled 0",
    "}",
    "",
    "apply_thermal() {",
    "  case \"\$P_THM\" in",
    "    shell)",
    "      for z in /sys/class/thermal/thermal_zone*/; do",
    "        [ -d \"\$z\" ] || continue",
    "        case \"\$(cat \"\$z/type\" 2>/dev/null)\" in",
    "          shell_front|shell_frame|shell_back) W \"\$z/mode\" disabled; W \"\$z/emul_temp\" 29500 ;;",
    "          # 仿 Scene Extreme GT：写 emul_temp 假低温（29.5℃），温控以为不热",
    "          battery|mtk-battery|usb|vtskin*) W \"\$z/emul_temp\" 29500 ;;",
    "        esac",
    "      done ;;",
    "    both|hard) for z in /sys/class/thermal/thermal_zone*/mode; do W \"\$z\" disabled; done ;;",
    "    tmsg)",
    "      # 温控/锁帧配置空文件占位（仿 Scene [模块]温控移除）",
    "      for f in /system/product/etc/displayconfig/*thermal*.xml \\",
    "               /system/vendor/etc/thermal/*thermal*fps*.xml \\",
    "               /system/vendor/etc/thermal/thermallevel_to_fps.xml; do",
    "        [ -f \"\$f\" ] || continue",
    "        [ -f /data/local/tmp/xy_empty ] || : > /data/local/tmp/xy_empty",
    "        mount --bind /data/local/tmp/xy_empty \"\$f\" 2>/dev/null",
    "      done",
    "      for base in /proc/thermal_message /sys/class/thermal/thermal_message /sys/devices/virtual/thermal/thermal_message; do",
    "        [ -d \"\$base\" ] || continue",
    "        for f in temp_state market_download_limit cpu_nolimit_temp; do",
    "          pf=\"\$base/\$f\"; [ -e \"\$pf\" ] || continue",
    "          umount \"\$pf\" 2>/dev/null",
    "          cp -f \"\$pf\" \"/dev/xy_tm_\$f\" 2>/dev/null",
    "          mount --bind \"/dev/xy_tm_\$f\" \"\$pf\" 2>/dev/null",
    "        done",
    "        [ -e \"\$base/temp_state\" ] && W \"\$base/temp_state\" 0",
    "        [ -e \"\$base/market_download_limit\" ] && W \"\$base/market_download_limit\" 0",
    "        [ -e \"\$base/cpu_nolimit_temp\" ] && W \"\$base/cpu_nolimit_temp\" 49500",
    "      done ;;",
    "  esac",
    "  case \"\$P_THM\" in",
    "    both|soft) for z in /sys/class/thermal/thermal_zone*/emul_temp; do W \"\$z\" 29500; done ;;",
    "  esac",
    "}",
    "",
    "apply_corectl() {",
    "  if [ -n \"\$P_CORE_CPU\" ]; then",
    "    echo \"\$P_CORE_CPU\" | tr ',' '\\n' | while IFS=: read -r cn spec; do",
    "      d=\"/sys/devices/system/cpu/cpu\$cn/core_ctl\"",
    "      [ -d \"\$d\" ] || continue",
    "      set -- \$(echo \"\$spec\" | tr '/' ' ')",
    "      W \"\$d/enable\" 1",
    "      [ -n \"\$2\" ] && W \"\$d/max_cpus\" \"\$2\"",
    "      [ -n \"\$1\" ] && W \"\$d/min_cpus\" \"\$1\"",
    "      [ -n \"\$3\" ] && W \"\$d/enable\" \"\$3\"",
    "      [ -f \"\$d/cpu_busy_up_thres\" ] && W \"\$d/cpu_busy_up_thres\" \"80 80\"",
    "      [ -f \"\$d/busy_down_thres\" ] && W \"\$d/busy_down_thres\" \"55 55\"",
    "      [ -f \"\$d/offline_delay_ms\" ] && W \"\$d/offline_delay_ms\" 24",
    "    done",
    "    case \"\$P_UCLAMP\" in keep) : ;; *) W /proc/sys/kernel/sched_util_clamp_min \"\$P_UCLAMP\"; W /proc/sys/kernel/sched_util_clamp_max 1024 ;; esac",
    "    return 0",
    "  fi",
    "  i=0",
    "  for d in /sys/devices/system/cpu/cpu*/core_ctl; do",
    "    [ -d \"\$d\" ] || continue",
    "    cn=\$(basename \$(dirname \"\$d\") | sed 's/cpu//')",
    "    if [ \"\$P_CORE\" = \"auto\" ]; then",
    "      case \"\$cn\" in 0) : ;; *) W \"\$d/enable\" 0 ;; esac",
    "    else",
    "      case \"\$cn\" in",
    "        0) set -- \$(echo \"\$P_CORE\" | cut -d, -f1 | tr '/' ' ')",
    "           W \"\$d/enable\" 1; [ -n \"\$2\" ] && W \"\$d/max_cpus\" \"\$2\"; [ -n \"\$1\" ] && W \"\$d/min_cpus\" \"\$1\"; [ -n \"\$3\" ] && W \"\$d/enable\" \"\$3\" ;;",
    "        4) set -- \$(echo \"\$P_CORE\" | cut -d, -f2 | tr '/' ' ')",
    "           W \"\$d/enable\" 1; [ -n \"\$2\" ] && W \"\$d/max_cpus\" \"\$2\"; [ -n \"\$1\" ] && W \"\$d/min_cpus\" \"\$1\"; [ -n \"\$3\" ] && W \"\$d/enable\" \"\$3\" ;;",
    "        7) set -- \$(echo \"\$P_CORE\" | cut -d, -f3 | tr '/' ' ')",
    "           W \"\$d/enable\" 1; [ -n \"\$2\" ] && W \"\$d/max_cpus\" \"\$2\"; [ -n \"\$1\" ] && W \"\$d/min_cpus\" \"\$1\"; [ -n \"\$3\" ] && W \"\$d/enable\" \"\$3\" ;;",
    "        *) W \"\$d/enable\" 1 ;;",
    "      esac",
    "    fi",
    "  done",
    "  case \"\$P_UCLAMP\" in",
    "    keep) : ;;",
    "    *) W /proc/sys/kernel/sched_util_clamp_min \"\$P_UCLAMP\"",
    "       W /proc/sys/kernel/sched_util_clamp_max 1024 ;;",
    "  esac",
    "}",
    "",
    "",
    "# ===== 禁用包（按机型分组，避免跨系统同名包误删）=====",
    "PKGS_MI=\"com.xiaomi.joyose com.miui.joyose com.miui.gamebooster com.miui.gameturbo com.miui.gamebox com.miui.gamebar com.xiaomi.gamecenter com.xiaomi.gamecenter.sdk com.xiaomi.migameservice com.miui.analytics com.miui.msa com.miui.newhome com.miui.battery com.miui.securitycenter com.miui.securityadd com.miui.pad.security com.miui.pad.assistant com.miui.pad.gamecenter\"",
    "PKGS_GREEN=\"com.oplus.games com.oplus.battery\"",
    "",
    "apply_pkgs() {",
    "  [ \"\$P_PKGS\" = \"1\" ] || return 0",
    "  case \"\$P_PKGSET\" in",
    "    mi)    LIST_PKG=\"\$PKGS_MI\" ;;",
    "    green) LIST_PKG=\"\$PKGS_GREEN\" ;;",
    "    *)     return 0 ;;    # 没指定机型组 → 不禁（防误删）",
    "  esac",
    "  : > /data/adb/xy_game_pkgs_disabled 2>/dev/null",
    "  for pkg in \$LIST_PKG; do",
    "    # 先确认包存在（不同系统同名包不误删）",
    "    pm list packages \"\$pkg\" 2>/dev/null | grep -q \"^package:\$pkg\$\" || continue",
    "    pm hide \"\$pkg\" >/dev/null 2>&1 && echo \"\$pkg:hide\" >> /data/adb/xy_game_pkgs_disabled",
    "  done",
    "}",
    "",
    "apply_hz() {",
    "  case \"\$P_HZ\" in",
    "    max)",
    "      F=\$(dumpsys display 2>/dev/null | grep -oE 'fps=[0-9.]+' | grep -oE '[0-9]+' | sort -n | tail -1)",
    "      [ -z \"\$F\" ] && F=\$(settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
    "      case \"\$F\" in ''|null|*[!0-9]*) F=120 ;; esac",
    "      settings put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "      settings put system min_refresh_rate \"\$F\" 2>/dev/null",
    "      settings --user 0 put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "      settings --user 0 put system min_refresh_rate \"\$F\" 2>/dev/null",
    "      return 0 ;;",
    "    ''|0|keep) return 0 ;;",
    "    *) F=\"\$P_HZ\"; settings put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "       settings put system min_refresh_rate \"\$F\" 2>/dev/null",
    "       settings put system user_refresh_rate \"\$F\" 2>/dev/null",
    "       settings --user 0 put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "       settings --user 0 put system min_refresh_rate \"\$F\" 2>/dev/null",
    "       echo \"\$F\" > /data/adb/xy_maxf 2>/dev/null",
    "       return 0 ;;",
    "  esac",
    "  F=\$(cat /data/adb/xy_maxf 2>/dev/null)",
    "  if [ -z \"\$F\" ]; then",
    "    F=\$(/system/bin/dumpsys SurfaceFlinger 2>/dev/null | grep -oE \"refresh-rate *[:=] *[0-9.]+\" | grep -oE \"[0-9]+\" | sort -n | tail -1)",
    "    [ -z \"\$F\" ] && F=\$(settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
    "    case \"\$F\" in ''|null|*[!0-9]*) F=120 ;; esac",
    "    echo \"\$F\" > /data/adb/xy_maxf 2>/dev/null",
    "  fi",
    "  settings put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "  settings put system min_refresh_rate \"\$F\" 2>/dev/null",
    "  settings put system user_refresh_rate \"\$F\" 2>/dev/null",
    "  settings --user 0 put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "  settings --user 0 put system min_refresh_rate \"\$F\" 2>/dev/null",
    "}",
    "",
    "apply_min_guard() {",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    mn=\$(cat \"\$p/cpuinfo_min_freq\" 2>/dev/null)",
    "    case \"\$P_MIN\" in",
    "      hw_min) want=\$mn ;;",
    "      max) want=\$hw ;;",
    "      *) want=\$((hw * P_MIN / 100)) ;;",
    "    esac",
    "    [ -n \"\$want\" ] && [ \"\$(cat \"\$p/scaling_min_freq\" 2>/dev/null)\" != \"\$want\" ] && W \"\$p/scaling_min_freq\" \"\$want\"",
    "  done",
    "}",
    "",
    "bind_threads() {",
    "  [ \"\$P_BIND\" = \"1\" ] || return 0",
    "  BR_F=0; BR_M=0; SEC_F=0; SEC_M=0",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    f=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$f\" ] || continue",
    "    cpus=\$(cat \"\$p/affected_cpus\" 2>/dev/null); [ -z \"\$cpus\" ] && cpus=\$(cat \"\$p/related_cpus\" 2>/dev/null)",
    "    [ -z \"\$cpus\" ] && continue",
    "    m=0; for c in \$cpus; do m=\$(( m | (1 << c) )); done",
    "    if [ \"\$f\" -gt \"\$BR_F\" ] 2>/dev/null; then SEC_F=\$BR_F; SEC_M=\$BR_M; BR_F=\$f; BR_M=\$m",
    "    elif [ \"\$f\" -gt \"\$SEC_F\" ] 2>/dev/null; then SEC_F=\$f; SEC_M=\$m; fi",
    "  done",
    "  [ \"\$BR_M\" = \"0\" ] && return 0",
    "  WANT=\$(printf '%x' \$(( BR_M | SEC_M )) | tr -d ' \\n')",
    "  WANTUI=\$(printf '%x' \$SEC_M | tr -d ' \\n')",
    "  _pid=\"\"",
    "  for tp in \$(cat /dev/cpuset/top-app/cgroup.procs 2>/dev/null); do",
    "    [ -d \"/proc/\$tp/task\" ] || continue",
    "    for t in /proc/\$tp/task/*; do",
    "      case \"\$(cat \$t/comm 2>/dev/null)\" in",
    "        UnityMain|UnityGfxDeviceW|UnitySRPWorker|RHIThread|GameThread|TaskGraphNP*) _pid=\$tp; break ;;",
    "      esac",
    "    done",
    "    [ -n \"\$_pid\" ] && break",
    "  done",
    "  [ -n \"\$_pid\" ] || return 0",
    "  for t in /proc/\$_pid/task/*; do",
    "    nm=\$(cat \"\$t/comm\" 2>/dev/null)",
    "    case \"\$nm\" in",
    "      RenderThread|UnityGfxDeviceW|UnitySRPWorker|RHIThread|GameThread|TaskGraphNP*)",
    "        timeout 1 taskset -p \"\$WANT\" \"\${t##*/}\" 2>/dev/null ;;",
    "      hwuiTask*) timeout 1 taskset -p \"\$WANTUI\" \"\${t##*/}\" 2>/dev/null ;;",
    "    esac",
    "  done",
    "}",
    "",
    "",
    "# ===== 记录原始状态（关闭时按机型精确恢复）=====",
    "SNAP=/data/adb/xy_orig_state",
    "snapshot_orig() {",
    "  : > \"\$SNAP\" 2>/dev/null",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    echo \"cpu|\$(basename \$p)|\$(cat \$p/scaling_max_freq 2>/dev/null)|\$(cat \$p/scaling_min_freq 2>/dev/null)|\$(cat \$p/scaling_governor 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  for z in /sys/class/thermal/thermal_zone*/; do",
    "    [ -d \"\$z\" ] || continue",
    "    echo \"thm|\$(basename \$z)|\$(cat \$z/mode 2>/dev/null)|\$(cat \$z/emul_temp 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  for d in background system-background foreground top-app; do",
    "    [ -d \"/dev/cpuset/\$d\" ] && echo \"cpset|\$d|\$(cat /dev/cpuset/\$d/cpus 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  for d in /sys/devices/system/cpu/cpu*/core_ctl; do",
    "    [ -d \"\$d\" ] || continue",
    "    echo \"cctl|\$(basename \$(dirname \$d))|\$(cat \$d/min_cpus 2>/dev/null)|\$(cat \$d/max_cpus 2>/dev/null)|\$(cat \$d/enable 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  [ -f /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent ] && echo \"gpu|mod|\$(cat /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /sys/class/kgsl/kgsl-3d0/devfreq/max_freq ] && echo \"gpu|maxfreq|\$(cat /sys/class/kgsl/kgsl-3d0/devfreq/max_freq 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /sys/class/kgsl/kgsl-3d0/devfreq/min_freq ] && echo \"gpu|minfreq|\$(cat /sys/class/kgsl/kgsl-3d0/devfreq/min_freq 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /proc/gpufreqv2/fix_target_opp_index ] && echo \"gpu|opp|\$(head -1 /proc/gpufreqv2/fix_target_opp_index 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /sys/module/ged/parameters/gpu_cust_upbound_freq ] && echo \"gpu|ged|\$(cat /sys/module/ged/parameters/gpu_cust_upbound_freq 2>/dev/null)\" >> \"\$SNAP\"",
    "  # GED boost 状态",
    "  for k in boost_gpu_enable ged_smart_boost ged_boost_enable; do",
    "    [ -f \"/sys/module/ged/parameters/\$k\" ] && echo \"ged|\$k|\$(cat /sys/module/ged/parameters/\$k 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  # 骁龙 force 节点",
    "  for k in force_clk_on force_bus_on force_rail_on min_pwrlevel max_pwrlevel; do",
    "    [ -f \"/sys/class/kgsl/kgsl-3d0/\$k\" ] && echo \"kgsl|\$k|\$(cat /sys/class/kgsl/kgsl-3d0/\$k 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  # DDR/DDRQOS/LLCC/L3",
    "  for bus in DDR DDRQOS LLCC L3; do",
    "    B=\"/sys/devices/system/cpu/bus_dcvs/\$bus\"",
    "    [ -f \"\$B/boost_freq\" ] && echo \"bus|\$bus|\$(cat \"\$B/boost_freq\" 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  echo \"hz|peak|\$(settings get system peak_refresh_rate 2>/dev/null)\" >> \"\$SNAP\"",
    "  echo \"hz|min|\$(settings get system min_refresh_rate 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /proc/powerhal_cpu_ctrl/perfserv_freq ] && echo \"ph|perfserv|\$(cat /proc/powerhal_cpu_ctrl/perfserv_freq 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /proc/powerhal_cpu_ctrl/adpf_enable ] && echo \"ph|adpf|\$(cat /proc/powerhal_cpu_ctrl/adpf_enable 2>/dev/null)\" >> \"\$SNAP\"",
    "  [ -f /sys/module/perfmgr/parameters/perfmgr_enable ] && echo \"pm|enable|\$(cat /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null)\" >> \"\$SNAP\"",
    "  # walt 调度参数",
    "  for w in sched_boost walt_rtg_cfs_boost_prio walt_low_latency_task_threshold; do",
    "    [ -f \"/proc/sys/walt/\$w\" ] && echo \"walt|\$w|\$(cat /proc/sys/walt/\$w 2>/dev/null)\" >> \"\$SNAP\"",
    "  done",
    "  echo \"uclamp|min|\$(cat /proc/sys/kernel/sched_util_clamp_min 2>/dev/null)\" >> \"\$SNAP\"",
    "  echo \"uclamp|max|\$(cat /proc/sys/kernel/sched_util_clamp_max 2>/dev/null)\" >> \"\$SNAP\"",
    "}",
    "",
    "# ===== 按记录的原始状态恢复（精确到机型）=====",
    "restore_orig() {",
    "  [ -f \"\$SNAP\" ] || return 0",
    "  while IFS='|' read -r kind a b c d; do",
    "    case \"\$kind\" in",
    "      cpu)",
    "        p=\"/sys/devices/system/cpu/cpufreq/\$a\"",
    "        [ -d \"\$p\" ] || continue",
    "        chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" \"\$p/scaling_governor\" 2>/dev/null",
    "        [ -n \"\$b\" ] && echo \"\$b\" > \"\$p/scaling_max_freq\" 2>/dev/null",
    "        [ -n \"\$c\" ] && echo \"\$c\" > \"\$p/scaling_min_freq\" 2>/dev/null",
    "        [ -n \"\$d\" ] && echo \"\$d\" > \"\$p/scaling_governor\" 2>/dev/null",
    "        ;;",
    "      thm)",
    "        z=\"/sys/class/thermal/\$a\"",
    "        [ -d \"\$z\" ] || continue",
    "        [ -n \"\$b\" ] && { chmod 0644 \"\$z/mode\" 2>/dev/null; echo \"\$b\" > \"\$z/mode\" 2>/dev/null; }",
    "        [ -n \"\$c\" ] && echo \"\$c\" > \"\$z/emul_temp\" 2>/dev/null",
    "        ;;",
    "      cpset)",
    "        [ -d \"/dev/cpuset/\$a\" ] && [ -n \"\$b\" ] && echo \"\$b\" > \"/dev/cpuset/\$a/cpus\" 2>/dev/null",
    "        ;;",
    "      cctl)",
    "        d2=\"/sys/devices/system/cpu/\$a/core_ctl\"",
    "        [ -d \"\$d2\" ] || continue",
    "        [ -n \"\$c\" ] && echo 1 > \"\$d2/enable\" 2>/dev/null",
    "        [ -n \"\$b\" ] && echo \"\$b\" > \"\$d2/max_cpus\" 2>/dev/null",
    "        [ -n \"\$a\" ] && echo \"\$a\" > /dev/null 2>&1",
    "        [ -n \"\$(echo \$b)\" ] && echo \"\$b\" > \"\$d2/min_cpus\" 2>/dev/null",
    "        [ -n \"\$c\" ] && [ \"\$c\" != \"1\" ] && echo \"\$c\" > \"\$d2/enable\" 2>/dev/null",
    "        ;;",
    "      gpu)",
    "        case \"\$a\" in",
    "          mod) [ -f /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent ] && echo \"\$b\" > /sys/class/kgsl/kgsl-3d0/devfreq/mod_percent 2>/dev/null ;;",
    "          maxfreq) [ -f /sys/class/kgsl/kgsl-3d0/devfreq/max_freq ] && echo \"\$b\" > /sys/class/kgsl/kgsl-3d0/devfreq/max_freq 2>/dev/null ;;",
    "          minfreq) [ -f /sys/class/kgsl/kgsl-3d0/devfreq/min_freq ] && echo \"\$b\" > /sys/class/kgsl/kgsl-3d0/devfreq/min_freq 2>/dev/null ;;",
    "          ged) [ -f /sys/module/ged/parameters/gpu_cust_upbound_freq ] && echo \"\$b\" > /sys/module/ged/parameters/gpu_cust_upbound_freq 2>/dev/null ;;",
    "          opp) [ -f /proc/gpufreqv2/fix_target_opp_index ] && [ -n \"\$b\" ] && echo \"\$b\" > /proc/gpufreqv2/fix_target_opp_index 2>/dev/null ;;",
    "        esac",
    "        ;;",
    "      hz)",
    "        case \"\$a\" in",
    "          peak) [ -n \"\$b\" ] && [ \"\$b\" != \"null\" ] && { settings --user 0 put system peak_refresh_rate \"\$b\" 2>/dev/null || settings put system peak_refresh_rate \"\$b\" 2>/dev/null; } ;;",
    "          min)  [ -n \"\$b\" ] && [ \"\$b\" != \"null\" ] && { settings --user 0 put system min_refresh_rate \"\$b\" 2>/dev/null || settings put system min_refresh_rate \"\$b\" 2>/dev/null; } ;;",
    "        esac",
    "        ;;",
    "      ph)",
    "        case \"\$a\" in",
    "          perfserv) [ -f /proc/powerhal_cpu_ctrl/perfserv_freq ] && echo \"\$b\" > /proc/powerhal_cpu_ctrl/perfserv_freq 2>/dev/null ;;",
    "          adpf) [ -f /proc/powerhal_cpu_ctrl/adpf_enable ] && echo \"\$b\" > /proc/powerhal_cpu_ctrl/adpf_enable 2>/dev/null ;;",
    "        esac",
    "        ;;",
    "      pm)",
    "        [ -f /sys/module/perfmgr/parameters/perfmgr_enable ] && echo \"\$b\" > /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null",
    "        ;;",
    "      ged)",
    "        [ -f \"/sys/module/ged/parameters/\$a\" ] && echo \"\$b\" > \"/sys/module/ged/parameters/\$a\" 2>/dev/null",
    "        ;;",
    "      kgsl)",
    "        [ -f \"/sys/class/kgsl/kgsl-3d0/\$a\" ] && echo \"\$b\" > \"/sys/class/kgsl/kgsl-3d0/\$a\" 2>/dev/null",
    "        ;;",
    "      walt)",
    "        [ -f \"/proc/sys/walt/\$a\" ] && echo \"\$b\" > \"/proc/sys/walt/\$a\" 2>/dev/null",
    "        ;;",
    "      bus)",
    "        B=\"/sys/devices/system/cpu/bus_dcvs/\$a\"",
    "        [ -f \"\$B/boost_freq\" ] && echo \"\$b\" > \"\$B/boost_freq\" 2>/dev/null",
    "        ;;",
    "      uclamp)",
    "        case \"\$a\" in",
    "          min) echo \"\$b\" > /proc/sys/kernel/sched_util_clamp_min 2>/dev/null ;;",
    "          max) echo \"\$b\" > /proc/sys/kernel/sched_util_clamp_max 2>/dev/null ;;",
    "        esac",
    "        ;;",
    "    esac",
    "  done < \"\$SNAP\"",
    "}",
    "",
    "",
    "# ===== 通用杂项优化（KSM/日志/调度）=====",
    "apply_extra() {",
    "  # 1. 关闭 KSM（省 CPU）",
    "  W /sys/kernel/mm/ksm/run 0",
    "  # 2. 关闭 zram 写回（如可）",
    "  W /sys/block/zram0/writeback 0",
    "  # 4. walt 调度增强",
    "  W /proc/sys/walt/sched_boost 1",
    "  W /proc/sys/kernel/sched_boost 1",
    "  # 5. 关闭内核软锁检测（减少抖动）",
    "  W /proc/sys/kernel/softlockup_panic 0",
    "  # 6. IO 调度：无",
    "}",
    "",
    "# ===== 骁龙 内存/缓存频率增强（DDR/DDRQOS/LLCC/L3）=====",
    "apply_sd_extra() {",
    "  for bus in DDR DDRQOS LLCC L3; do",
    "    B=\"/sys/devices/system/cpu/bus_dcvs/\$bus\"",
    "    [ -d \"\$B\" ] || continue",
    "    _max=\$(cat \"\$B/available_frequencies\" 2>/dev/null | tr ' ' '\\n' | grep -E '^[0-9]+\$' | sort -n | tail -1)",
    "    [ -n \"\$_max\" ] && {",
    "      W \"\$B/boost_freq\" \"\$_max\"",
    "      W \"\$B/max_freq\" \"\$_max\"",
    "    }",
    "  done",
    "  # Perfmgr（小米 FEAS）关闭：让 CPU 不受 FEAS 限制",
    "  W /sys/module/perfmgr/parameters/perfmgr_enable 0",
    "  W /sys/module/perfmgr/parameters/max_freq_limit_level 0",
    "}",
    "",
    "apply_all() {",
    "  echo \"[apply] \$PROFILE\"",
    "  apply_cpu; apply_tl; apply_cpuset; apply_gpu; apply_perfserv",
    "  apply_thermal; apply_extra; apply_sd_extra; apply_corectl; apply_pkgs; apply_hz; bind_threads",
    "  echo \"[apply] done\"",
    "}",
    "",
    "# ============ 五、守护（只维持前台值，不切前后台）============",
    "# restore 子命令：按记录的原始状态精确恢复（按机型）",
    "if [ \"\$ACTION\" = \"restore\" ]; then",
    "  echo \"[restore] 按原始快照恢复...\"",
    "  restore_orig",
    "  echo \"[restore] 完成\"",
    "  rm -f \"\$SNAP\" 2>/dev/null",
    "  exit 0",
    "fi",
    "",
    "[ -f \"\$SNAP\" ] || snapshot_orig",
    "apply_all",
    "tick=0",
    "while true; do",
    "  [ -f /data/adb/xy_game.off ] && exit 0",
    "  tick=\$((tick+1))",
    "  if [ -f /data/adb/xy_freq_lock ]; then",
    "    while read -r pol m; do",
    "      [ -n \"\$pol\" ] && [ -n \"\$m\" ] || continue",
    "      p=\"/sys/devices/system/cpu/cpufreq/\$pol/scaling_max_freq\"",
    "      [ -f \"\$p\" ] || continue",
    "      # 只在偏离时写一次（MTK 频率写入有节流，猛写会乱）",
    "      if [ \"\$(cat \"\$p\" 2>/dev/null)\" != \"\$m\" ]; then",
    "        chmod 0666 \"\$p\" 2>/dev/null",
    "        echo \"\$m\" > \"\$p\" 2>/dev/null",
    "      fi",
    "    done < /data/adb/xy_freq_lock",
    "  fi",
    "  # 每轮维持 GED（系统会改回 1300000，导致 p0 被压 1.1G）",
    "  [ -n \"\$P_GPOUP\" ] && [ \"\$P_GPOUP\" != \"0\" ] && W /sys/module/ged/parameters/gpu_cust_upbound_freq \"\$P_GPOUP\"",
    "  if [ \$((tick % 10)) = 1 ]; then",
    "    bind_threads",
    "    apply_cpuset                 # ColorOS 会改回，必须维持",
    "    [ \"\$P_GPOUP\" != \"0\" ] && W /sys/module/ged/parameters/gpu_cust_upbound_freq \"\$P_GPOUP\"",
    "    # rate 维持（系统会改回）—— P_UP/P_DOWN 优先",
    "    _ru=\"\$P_UP\"; _rd=\"\$P_DOWN\"",
    "    [ -z \"\$_ru\" ] && [ -n \"\$P_RATE\" ] && [ \"\$P_RATE\" != \"keep\" ] && { _ru=\"\$P_RATE\"; _rd=\"\$P_RATE\"; }",
    "    if [ -n \"\$_ru\" ]; then",
    "      for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "        [ -d \"\$p\" ] || continue",
    "        for sub in sugov_ext walt; do",
    "          [ -f \"\$p/\$sub/up_rate_limit_us\" ] && [ \"\$(cat \$p/\$sub/up_rate_limit_us 2>/dev/null)\" != \"\$_ru\" ] && W \"\$p/\$sub/up_rate_limit_us\" \"\$_ru\"",
    "          [ -n \"\$_rd\" ] && [ -f \"\$p/\$sub/down_rate_limit_us\" ] && [ \"\$(cat \$p/\$sub/down_rate_limit_us 2>/dev/null)\" != \"\$_rd\" ] && W \"\$p/\$sub/down_rate_limit_us\" \"\$_rd\"",
    "        done",
    "      done",
    "    fi",
    "  fi",
    "  # 遮罩每 30 秒检查一次（掉了才重挂）",
    "  if [ \$((tick % 150)) = 1 ] && [ \"\$P_PS\" = \"clear\" ]; then",
    "    apply_ps_mask",
    "    apply_pmi_mask",
    "  fi",
    "  sleep 0.2",
    "done",
    ""
)
