package me.weishu.kernelsu.ui.screen

import android.os.Build
import android.graphics.Typeface
import android.view.MotionEvent
import android.util.TypedValue
import android.view.Gravity
import android.graphics.PixelFormat
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Notification
import android.os.IBinder
import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import android.widget.TextView
import android.view.WindowManager
import android.app.Service
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
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
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
import androidx.compose.ui.window.DialogProperties
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
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
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

    // 处理外部应用传来的文件/目录（ACTION_VIEW）
    val navCtx0 = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        try {
            val act = navCtx0 as? android.app.Activity
            val uri = act?.intent?.data
            if (uri != null) {
                // 不用 MediaStore（可能返回同名错误文件），直接按来源 App 目录搜
                var fp = if (uri.scheme == "file") uri.path else null
                // 真实路径拿不到时，按来源 App 的目录搜索
                if (fp.isNullOrBlank() || !java.io.File(fp).exists()) {
                    val name = queryDisplayName(navCtx0, uri) ?: uri.lastPathSegment
                    if (!name.isNullOrBlank()) {
                        val pkg = try {
                            act?.referrer?.host ?: act?.intent?.`package` ?: ""
                        } catch (t: Throwable) { "" }
                        val roots = when {
                            pkg.contains("mobileqq") -> listOf(
                                "/storage/emulated/0/tencent/QQfile_recv",
                                "/storage/emulated/0/tencent/QQ_Images",
                                "/storage/emulated/0/Android/data/com.tencent.mobileqq")
                            pkg.contains("weixin") || pkg.contains("tencent.mm") -> listOf(
                                "/storage/emulated/0/tencent/MicroMsg/Download",
                                "/storage/emulated/0/tencent/MicroMsg/WeiXin",
                                "/storage/emulated/0/Android/data/com.tencent.mm")
                            pkg.contains("baidu") -> listOf("/storage/emulated/0/BaiduNetdisk")
                            pkg.contains("taobao") || pkg.contains("alibaba") -> listOf("/storage/emulated/0/Android/data/com.taobao.taobao")
                            pkg.contains("ucmobile") -> listOf("/storage/emulated/0/UCDownloads")
                            pkg.contains("browser") || pkg.contains("chrome") -> listOf("/storage/emulated/0/Download")
                            else -> listOf("/storage/emulated/0/Download", "/storage/emulated/0")
                        }
                        val found = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            for (root in roots) {
                                val hit = suNow("find '" + root + "' -maxdepth 4 -name '" + name.replace("'", "'\\''") + "' 2>/dev/null | head -1").trim()
                                if (hit.isNotEmpty()) return@withContext hit
                            }
                            ""
                        }
                        if (found.isNotEmpty()) {
                            fp = found
                        } else {
                            // 回退：全盘按文件名搜
                            val global = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                suNow("find /storage/emulated/0 -maxdepth 6 -name '" + name.replace("'", "'\\''") + "' 2>/dev/null | head -1").trim()
                            }
                            if (global.isNotEmpty()) fp = global
                        }
                    }
                }
                if (!fp.isNullOrBlank()) {
                    val f = java.io.File(fp)
                    if (f.isDirectory) {
                        leftPath = fp
                        FileLocate.path = fp
                        FileLocate.tick += 1
                    } else {
                        val pdir = f.parent
                        if (!pdir.isNullOrBlank()) {
                            leftPath = pdir
                            FileLocate.path = fp
                            FileLocate.tick += 1
                        }
                    }
                }
            }
        } catch (t: Throwable) {}
    }

    var panel by remember { mutableStateOf(false) }
    var showTerminal by remember { mutableStateOf(false) }
    var previewPath by remember { mutableStateOf<String?>(null) }
    var archiveReturnPath by remember { mutableStateOf<String?>(null) }
    var termFull by remember { mutableStateOf(false) }
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
        // 存完整路径（不是文件名），避免同名文件跳错
        leftHighlight = target
    }

    var runMessage by remember { mutableStateOf<String?>(null) }
    var runOk by remember { mutableStateOf(true) }
    var running by remember { mutableStateOf(false) }
    var activeProc by remember { mutableStateOf<Process?>(null) }
    val panelP = remember { Animatable(0f) }
    val uiScope = rememberCoroutineScope()

    // 执行命令（岛内和全屏共用）
    fun termRun() {
        val cmd = termInput
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

    // 目录滚动位置（根层共享，避免 key 重建丢状态）
    val dirScroll = remember { mutableStateMapOf<String, Int>() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 视频背景（TextureView + MediaPlayer，循环静音，裁切填满）
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { ctx ->
                // 图片背景（bg_image.img）优先，否则播视频（bg_video.mp4）
                val imgCached = java.io.File(ctx.filesDir, "bg_image.img")
                if (imgCached.exists() && imgCached.length() > 0L) {
                    val iv = android.widget.ImageView(ctx)
                    // default=等比例撑满；landscape=把图片旋转成横屏再撑满
                    val bgMode = try {
                        ctx.getSharedPreferences("settings", 0).getString("bg_mode", "default") ?: "default"
                    } catch (t: Throwable) { "default" }
                    iv.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    iv.background = android.graphics.drawable.ColorDrawable(android.graphics.Color.BLACK)
                    try {
                        if (bgMode == "landscape") {
                            // 横屏模式：把图片旋转 90°
                            val raw = android.graphics.BitmapFactory.decodeFile(imgCached.absolutePath)
                            if (raw != null) {
                                val mtx = android.graphics.Matrix()
                                mtx.postRotate(-90f)
                                val rotated = android.graphics.Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, mtx, true)
                                iv.setImageBitmap(rotated)
                            } else {
                                iv.setImageBitmap(android.graphics.BitmapFactory.decodeFile(imgCached.absolutePath))
                            }
                        } else {
                            iv.setImageBitmap(android.graphics.BitmapFactory.decodeFile(imgCached.absolutePath))
                        }
                    } catch (t: Throwable) {}
                    iv
                } else {
                val tv = android.view.TextureView(ctx)
                // 横屏模式：视频旋转 90°
                val bgModeV = try {
                    ctx.getSharedPreferences("settings", 0).getString("bg_mode", "default") ?: "default"
                } catch (t: Throwable) { "default" }
                if (bgModeV == "landscape") {
                    tv.rotation = 90f
                    tv.scaleX = 1.25f
                    tv.scaleY = 1.25f
                }
                val cached = java.io.File(ctx.filesDir, "bg_video.mp4")
                if (!cached.exists() || cached.length() == 0L) {
                    // 1) 优先从 小鱼儿/bg.mp4 复制
                    try {
                        val p = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                            "mkdir -p '" + ctx.filesDir.absolutePath + "' && cp '/storage/emulated/0/小鱼儿/bg.mp4' '" + cached.absolutePath + "' && chmod 644 '" + cached.absolutePath + "'"))
                        p.waitFor()
                    } catch (t: Throwable) {}
                }
                if (!cached.exists() || cached.length() == 0L) {
                    // 2) 回退：从 APK 内置 assets/bg.mp4 释放
                    try {
                        ctx.assets.open("bg.mp4").use { input ->
                            cached.outputStream().use { out -> input.copyTo(out) }
                        }
                    } catch (t: Throwable) {
                        android.util.Log.e("BG", "asset: " + (t.message ?: ""))
                    }
                }
                tv.surfaceTextureListener = object : android.view.TextureView.SurfaceTextureListener {
                    var mp: android.media.MediaPlayer? = null
                    override fun onSurfaceTextureAvailable(st: android.graphics.SurfaceTexture, w: Int, h: Int) {
                        try {
                            val m = android.media.MediaPlayer()
                            m.setDataSource(cached.absolutePath)
                            m.setSurface(android.view.Surface(st))
                            m.isLooping = true
                            m.setVolume(0f, 0f)
                            m.setOnErrorListener { _, what, extra ->
                                android.widget.Toast.makeText(ctx, "视频错误 " + what + "/" + extra, android.widget.Toast.LENGTH_LONG).show()
                                true
                            }
                            m.prepare()
                            m.start()
                            mp = m
                        } catch (t: Throwable) {
                            // 播放失败：删掉缓存，下次启动重新复制（可能缓存损坏/编码不兼容）
                            try { cached.delete() } catch (t2: Throwable) {}
                            android.widget.Toast.makeText(ctx, "视频异常[" + t.javaClass.simpleName + "]: " + (t.message ?: ""), android.widget.Toast.LENGTH_LONG).show()
                            try {
                                val lf = java.io.File("/storage/emulated/0/小鱼儿/bg_error.log")
                                lf.appendText(java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date()) +
                                    " " + t.javaClass.simpleName + ": " + (t.message ?: "") +
                                    " | file=" + cached.absolutePath + " | len=" + cached.length() +
                                    " | exists=" + cached.exists() + "\n")
                            } catch (t2: Throwable) {}
                        }
                    }
                    override fun onSurfaceTextureSizeChanged(st: android.graphics.SurfaceTexture, w: Int, h: Int) {}
                    override fun onSurfaceTextureDestroyed(st: android.graphics.SurfaceTexture): Boolean {
                        try { mp?.release() } catch (t: Throwable) {}
                        mp = null
                        return true
                    }
                    override fun onSurfaceTextureUpdated(st: android.graphics.SurfaceTexture) {}
                }
                tv
                }
            },
            modifier = Modifier.fillMaxSize()
                .blur(
                    // 编辑/终端/预览/能量守恒界面 30dp；其余时间按「背景模糊」开关 20dp
                    if (editPath != null || termFull || previewPath != null || panel) 30.dp
                    else if (try { appCtx.getSharedPreferences("settings", 0).getBoolean("bg_blur", true) }
                             catch (t: Throwable) { true }) 20.dp
                    else 0.dp
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    // 编辑/终端/预览/能量守恒时对文件管理器固定 30dp 模糊（不受开关控制）
                    if (panel || termFull || editPath != null || previewPath != null) 30.dp else 0.dp
                )
        ) {
    Row(modifier = Modifier.fillMaxSize()) {
        key(leftPath) {
            BrowserPane(
                path = leftPath,
                dirScroll = dirScroll,
                onNavigate = { np ->
                    // 从压缩包临时目录返回 → 回到压缩包原目录
                    if (leftPath == "/data/local/tmp/yx_arch" && np.startsWith("/data/local/tmp")) {
                        leftPath = archiveReturnPath ?: np
                    } else {
                        leftPath = np
                    }
                },
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
                dirScroll = dirScroll,
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
        onOpenTerminal = { termFull = true },
        onPreview = { previewPath = it },
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
            // 定位（高亮）时没有选中态，回退用 leftHighlight 路径
            val p = longPressPath ?: selectedFile ?: leftHighlight
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

    // 图片/视频预览
    val pv = previewPath
    if (pv != null) {
        val pvLower = pv.lowercase()
        val pvIsVideo = pvLower.endsWith(".mp4") || pvLower.endsWith(".mkv") ||
            pvLower.endsWith(".avi") || pvLower.endsWith(".mov") ||
            pvLower.endsWith(".webm") || pvLower.endsWith(".3gp")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(200f)
                .background(Color.Black)
                .clickable { previewPath = null },
            contentAlignment = Alignment.Center
        ) {
            if (pvIsVideo) {
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { vctx ->
                        android.widget.VideoView(vctx).apply {
                            val tmp = java.io.File(vctx.cacheDir, "preview_${Math.abs(pv.hashCode())}.mp4")
                            if (!tmp.exists()) {
                                try {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                                        "cp '" + pv.replace("'", "'\\''") + "' '" + tmp.absolutePath + "'"))
                                    pr.waitFor()
                                } catch (t: Throwable) {}
                            }
                            setVideoURI(android.net.Uri.fromFile(tmp))
                            setOnPreparedListener { mp -> mp.isLooping = true; start() }
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(vertical = 60.dp)
                )
            } else {
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { vctx ->
                        android.widget.ImageView(vctx).apply {
                            val tmp = java.io.File(vctx.cacheDir, "preview_${Math.abs(pv.hashCode())}.img")
                            if (!tmp.exists()) {
                                try {
                                    val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
                                        "cp '" + pv.replace("'", "'\\''") + "' '" + tmp.absolutePath + "'"))
                                    pr.waitFor()
                                } catch (t: Throwable) {}
                            }
                            setImageBitmap(android.graphics.BitmapFactory.decodeFile(tmp.absolutePath))
                            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(vertical = 60.dp)
                )
            }
        }
    }

    // 全屏终端（在模糊层之外，输出清晰）
    if (termFull) {
        TerminalOverlay(
            text = termText,
            input = termInput,
            onInputChange = { termInput = it },
            onRun = { termRun() },
            onExit = { termFull = false }
        )
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
        var editFontScale by remember { mutableStateOf(1f) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "编辑 " + (editPath ?: "").substringAfterLast('/'),
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "取消",
                        color = Color(0xFFAAAAAA),
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { editPath = null }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                    Text(
                        text = "保存",
                        color = Color(0xFF0A84FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
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
                                                        target.replace("'", "'\\''") + "'"
                                                ).exec()
                                            } catch (t: Throwable) {
                                            }
                                        }
                                        editPath = null
                                        refreshTick += 1
                                    }
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = (12f * editFontScale).sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            // 只在双指时缩放，单指放行给滚动
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                do {
                                    val ev = awaitPointerEvent()
                                    if (ev.changes.size >= 2) {
                                        val zoom = ev.calculateZoom()
                                        if (zoom != 1f && zoom > 0f) {
                                            editFontScale = (editFontScale * zoom).coerceIn(0.6f, 3f)
                                            ev.changes.forEach { it.consume() }
                                        }
                                    }
                                } while (ev.changes.any { it.pressed })
                            }
                        }
                        .padding(10.dp)
                )
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
    onOpenTerminal: () -> Unit,
    onPreview: (String) -> Unit,
    onOpen: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var bgModePick by remember { mutableStateOf<String?>(null) }
    var bgModeIsImg by remember { mutableStateOf(false) }
    val bgCtx = androidx.compose.ui.platform.LocalContext.current
    var bgBlurOn by remember {
        mutableStateOf(
            bgCtx.getSharedPreferences("settings", 0).getBoolean("bg_blur", true)
        )
    }
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
    var wingIconExpanded by remember { mutableStateOf(false) }
    val wingHidden = selectedFile != null || navMode
    LaunchedEffect(wingHidden) {
        if (wingHidden) {
            ball.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 120f))
        } else {
            ball.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 130f))
        }
    }
    // 执行文件时：分岛从胶囊里滑出（复用显出的动画）
    LaunchedEffect(terminalMode) {
        if (terminalMode) {
            wingIconExpanded = true
            ball.snapTo(0f)
            ball.animateTo(1f, spring(dampingRatio = 0.7f, stiffness = 130f))
        } else {
            // 收起：分岛按原速滑回胶囊（保持放大图标），滑出前才变层叠
            ball.animateTo(0f, spring(dampingRatio = 0.72f, stiffness = 120f))
            wingIconExpanded = false
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
    val _lowerSel2 = selName.lowercase()
    val _isImgSel = _lowerSel2.endsWith(".jpg") || _lowerSel2.endsWith(".jpeg") || _lowerSel2.endsWith(".png") ||
        _lowerSel2.endsWith(".webp") || _lowerSel2.endsWith(".bmp") || _lowerSel2.endsWith(".gif")
    val _isVidSel = _lowerSel2.endsWith(".mp4") || _lowerSel2.endsWith(".mkv") || _lowerSel2.endsWith(".avi") ||
        _lowerSel2.endsWith(".mov") || _lowerSel2.endsWith(".webm") || _lowerSel2.endsWith(".3gp")
    val actionLabel = when {
        sel == null -> ""
        _isImgSel || _isVidSel -> "查看"
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
                .clickable {
                    if (wingIconExpanded) {
                        // 先关岛内终端 + 清长按/选中 + 收岛，再由全屏终端接管
                        onTermClose()
                        onClearLongPress()
                        onClearSelection()
                        closeIsland()
                        onOpenTerminal()
                    } else onOpen()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (wingIconExpanded) {
                Icon(
                    imageVector = Icons.Outlined.Fullscreen,
                    contentDescription = "放大",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = me.weishu.kernelsu.R.mipmap.mod_icon),
                    contentDescription = null,
                    modifier = Modifier.size(34.dp)
                )
            }
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
                            var fpsShow by remember {
                                mutableStateOf(
                                    try {
                                        navCtx.getSharedPreferences("settings", 0).getBoolean("fps_show", false)
                                    } catch (t: Throwable) { false }
                                )
                            }
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
                                            fpsShow = !fpsShow
                                            navCtx.getSharedPreferences("settings", 0).edit()
                                                .putBoolean("fps_show", fpsShow).apply()
                                            try {
                                                if (fpsShow) {
                                                    if (!android.provider.Settings.canDrawOverlays(navCtx)) {
                                                        // 需要悬浮窗权限
                                                        val it = android.content.Intent(
                                                            android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                            android.net.Uri.parse("package:" + navCtx.packageName)
                                                        )
                                                        it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        navCtx.startActivity(it)
                                                    }
                                                    navCtx.startForegroundService(
                                                        android.content.Intent(navCtx, FpsOverlayService::class.java)
                                                    )
                                                } else {
                                                    navCtx.stopService(
                                                        android.content.Intent(navCtx, FpsOverlayService::class.java)
                                                    )
                                                }
                                            } catch (t: Throwable) {}
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "显示帧率\n在文件管理器顶部实时显示当前帧率",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "开", fontSize = 12.sp,
                                            color = if (fpsShow) Color.White else Color(0xFF5A5A5E))
                                        Text(text = " | ", fontSize = 12.sp, color = Color(0xFF5A5A5E))
                                        Text(text = "关", fontSize = 12.sp,
                                            color = if (fpsShow) Color(0xFF5A5A5E) else Color.White)
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
                                13 -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                IslandActionButton("默认", Color(0xFF3A3A3C)) {
                                                    setBgBackground(navCtx, bgModePick ?: "", bgModeIsImg, "default")
                                                    bgModePick = null
                                                    onClearLongPress()
                                                    closeIsland()
                                                }
                                            }
                                            Box(modifier = Modifier.weight(1f)) {
                                                IslandActionButton("横屏", Color(0xFF0A84FF)) {
                                                    setBgBackground(navCtx, bgModePick ?: "", bgModeIsImg, "landscape")
                                                    bgModePick = null
                                                    onClearLongPress()
                                                    closeIsland()
                                                }
                                            }
                                        }
                                        // 背景模糊开关（默认开）
                                        IslandActionButton(
                                            if (bgBlurOn) "背景模糊  开" else "背景模糊  关",
                                            Color(0xFF3A3A3C)
                                        ) {
                                            bgBlurOn = !bgBlurOn
                                            try {
                                                navCtx.getSharedPreferences("settings", 0).edit()
                                                    .putBoolean("bg_blur", bgBlurOn).apply()
                                            } catch (t: Throwable) {}
                                            android.widget.Toast.makeText(
                                                navCtx,
                                                if (bgBlurOn) "背景模糊：已开启" else "背景模糊：已关闭",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
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
                                            // 图片/视频设为背景
                                            if (!longPressIsDir && longPressPath != null) {
                                                val lp = longPressPath.lowercase()
                                                val isImg = lp.endsWith(".jpg") || lp.endsWith(".jpeg") || lp.endsWith(".png") || lp.endsWith(".webp") || lp.endsWith(".bmp")
                                                val isVid = lp.endsWith(".mp4") || lp.endsWith(".mkv") || lp.endsWith(".avi") || lp.endsWith(".mov") || lp.endsWith(".webm") || lp.endsWith(".3gp")
                                                if (isImg || isVid) {
                                                    // 点「设为背景」→ 收起岛 → 展开 → 显示两个模式按钮
                                                    IslandActionButton("设为背景", Color(0xFF0A84FF)) {
                                                        bgModePick = longPressPath
                                                        bgModeIsImg = isImg
                                                        switchStep(13)
                                                    }
                                                }
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
                                if (actionLabel == "查看") {
                                    sel?.let { onPreview(it) }
                                } else if (actionLabel == "更多") openArchive()
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
private fun TerminalOverlay(
    text: String,
    input: String,
    onInputChange: (String) -> Unit,
    onRun: () -> Unit,
    onExit: () -> Unit
) {
    val scroll = rememberScrollState()
    var fontScale by remember { mutableStateOf(1f) }
    var open by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { open = true }
    LaunchedEffect(text) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    // 和编辑文本同款：半透明黑 + 根层模糊背景
    androidx.compose.animation.AnimatedVisibility(
        visible = open,
        modifier = Modifier.fillMaxSize().zIndex(100f),
        enter = androidx.compose.animation.expandVertically(
            animationSpec = tween(360, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)),
            expandFrom = Alignment.CenterVertically
        ) + androidx.compose.animation.fadeIn(tween(200)),
        exit = androidx.compose.animation.shrinkVertically(
            animationSpec = tween(260, easing = CubicBezierEasing(0.32f, 0.72f, 0f, 1f)),
            shrinkTowards = Alignment.CenterVertically
        ) + androidx.compose.animation.fadeOut(tween(160))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SelectionContainer(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    Text(
                        text = ansiAnnotated(text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(requireUnconsumed = false)
                                    do {
                                        val ev = awaitPointerEvent()
                                        if (ev.changes.size >= 2) {
                                            val zoom = ev.calculateZoom()
                                            if (zoom != 1f && zoom > 0f) {
                                                fontScale = (fontScale * zoom).coerceIn(0.6f, 3f)
                                                ev.changes.forEach { it.consume() }
                                            }
                                        }
                                    } while (ev.changes.any { it.pressed })
                                }
                            }
                            .verticalScroll(scroll),
                        fontSize = (12f * fontScale).sp,
                        lineHeight = (18f * fontScale).sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFDDDDDD)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = input,
                        onValueChange = onInputChange,
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0E0E10), RoundedCornerShape(6.dp))
                            .padding(6.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = onExit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.FullscreenExit,
                            contentDescription = "缩小",
                            tint = Color(0xFFB0B0B0)
                        )
                    }
                    Text(
                        "发送",
                        modifier = Modifier
                            .clickable { onRun() }
                            .padding(start = 6.dp)
                            .background(Color(0xFF0A84FF), RoundedCornerShape(percent = 50))
                            .padding(horizontal = 16.dp, vertical = 7.dp),
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
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
            ModeButton(Icons.Outlined.AcUnit, "能量守恒", subtitle = devLabel)
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
        if (title == "能量守恒") {
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
                                // 优先使用 /sdcard 上的外部脚本（热更新：改脚本不用重新打包 APK）
                                val _ext = java.io.File("/sdcard/极道帝兵_性能模式.sh")
                                val _code = if (_ext.exists() && _ext.length() > 2000) {
                                    try { _ext.readText() } catch (t: Throwable) { perfGameScript.joinToString("\n") }
                                } else perfGameScript.joinToString("\n")
                                scriptFile.writeText(_code)
                            } catch (t: Throwable) { }
                            val sp = scriptFile.absolutePath.replace("'", "'\\''")
                            val r1 = su("rm -f /data/adb/xy_game.off; cat '" + sp + "' > /data/adb/xy_perf_daemon; chmod 755 /data/adb/xy_perf_daemon; ls -l /data/adb/xy_perf_daemon 2>/dev/null | awk '{print \$5}'")
                            // 开启：直接启动（脚本内部会单例处理，并自动识别机型+写第6行+配置）
                            su("for p in \$(ps -A -o PID,ARGS 2>/dev/null | grep -E 'xy_perf_daemon' | grep -v grep | grep -v 'sh -c' | awk '{print \$1}'); do kill -9 \$p 2>/dev/null; done; sleep 0.2; setsid sh /data/adb/xy_perf_daemon >/dev/null 2>&1 &")
                            "已开启"
                        } else {
                            // ===== 1. 按机型精确恢复（脚本记录的原始快照）=====
                            val sf2 = java.io.File(ctx3.filesDir, "xy_game_daemon.sh")
                            try {
                                val _ext2 = java.io.File("/sdcard/极道帝兵_性能模式.sh")
                                val _code2 = if (_ext2.exists() && _ext2.length() > 2000) {
                                    try { _ext2.readText() } catch (t: Throwable) { perfGameScript.joinToString("\n") }
                                } else perfGameScript.joinToString("\n")
                                sf2.writeText(_code2)
                            } catch (t: Throwable) { }
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
    dirScroll: MutableMap<String, Int>,
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
    // 记住每个目录的滚动位置，返回上级时恢复而不是跳到顶部（根层共享）
    val scrollPositions = dirScroll
    var restoredPath by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(entries) {
        if (entries.isNotEmpty() && restoredPath != path) {
            val pos = scrollPositions[path] ?: 0
            listState.scrollToItem(pos.coerceIn(0, maxOf(0, entries.size)))
            restoredPath = path
        }
    }
    androidx.compose.runtime.DisposableEffect(path) {
        onDispose {
            scrollPositions[path] = listState.firstVisibleItemIndex
        }
    }
    LaunchedEffect(highlightName, entries) {
        // 优先按完整路径定位；匹配不到时回退到文件名
        val byPath = entries.indexOfFirst {
            val joined = if (path.endsWith("/")) path + it.name else "$path/${it.name}"
            joined == highlightName
        }
        val byName = if (byPath < 0 && highlightName != null) {
            val n = highlightName.substringAfterLast('/')
            entries.indexOfFirst { it.name == n }
        } else -1
        val idx = if (byPath >= 0) byPath else byName
        if (idx >= 0) {
            listState.animateScrollToItem(idx + 1)
        }
    }

        LazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize().nestedScroll(pullConn),
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
                if (highlight != null && (highlight == fullPath || highlight == name)) Color(0x3300BCD4)
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

    // 图片/视频缩略图
    val lowerName = name.lowercase()
    val isImageFile = lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
        lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
        lowerName.endsWith(".webp") || lowerName.endsWith(".bmp")
    val isVideoFile = lowerName.endsWith(".mp4") || lowerName.endsWith(".mkv") ||
        lowerName.endsWith(".avi") || lowerName.endsWith(".mov") ||
        lowerName.endsWith(".3gp") || lowerName.endsWith(".webm")
    val mediaThumb by produceState<ImageBitmap?>(null, fullPath) {
        if (value != null) return@produceState
        if (isDir || (!isImageFile && !isVideoFile)) return@produceState
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // App 无存储权限：先用 root 复制到 cache 再解码
                val tmp = java.io.File(ctx.cacheDir, "thumb_" + Math.abs(fullPath.hashCode()))
                if (!tmp.exists() || tmp.length() == 0L) {
                    suNow("cp '" + fullPath.replace("'", "'\\''") + "' '" + tmp.absolutePath + "'")
                }
                if (!tmp.exists() || tmp.length() == 0L) null
                else if (isImageFile) {
                    val bmp = android.graphics.BitmapFactory.decodeFile(tmp.absolutePath)
                    if (bmp != null) {
                        val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, 96, 96, true)
                        if (scaled !== bmp) bmp.recycle()
                        scaled.asImageBitmap()
                    } else null
                } else {
                    val retr = android.media.MediaMetadataRetriever()
                    retr.setDataSource(tmp.absolutePath)
                    val bmp = retr.getFrameAtTime(0)
                    retr.release()
                    if (bmp != null) {
                        val scaled = android.graphics.Bitmap.createScaledBitmap(bmp, 96, 96, true)
                        if (scaled !== bmp) bmp.recycle()
                        scaled.asImageBitmap()
                    } else null
                }
            } catch (t: Throwable) { null }
        }
    }
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
    val mediaThumbNow = mediaThumb
    if (mediaThumbNow != null) {
        Image(
            bitmap = mediaThumbNow,
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp))
        )
    } else if (apkIconNow != null) {
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

// 把 content:// URI 解析成真实文件路径（MediaStore / Downloads）
// 从 content:// URI 查显示文件名
// 设置背景（图片/视频），mode = default / landscape
internal fun setBgBackground(ctx: android.content.Context, src: String, isImg: Boolean, mode: String) {
    try {
        ctx.getSharedPreferences("settings", 0).edit().putString("bg_mode", mode).apply()
        val dstName = if (isImg) "bg_image.img" else "bg_video.mp4"
        val otherName = if (isImg) "bg_video.mp4" else "bg_image.img"
        val dst = java.io.File(ctx.filesDir, dstName).absolutePath
        val other = java.io.File(ctx.filesDir, otherName).absolutePath
        val pr = Runtime.getRuntime().exec(arrayOf(suBin(), "--mount-master", "-c",
            "rm -f '" + other + "' && cp -f '" + src.replace("'", "'\\''") + "' '" + dst + "' && chmod 644 '" + dst + "'"))
        pr.waitFor()
        android.widget.Toast.makeText(ctx, "已设为背景（" + (if (mode == "default") "默认" else "横屏") + "），重进生效", android.widget.Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
        android.widget.Toast.makeText(ctx, "设置失败: " + (t.message ?: ""), android.widget.Toast.LENGTH_SHORT).show()
    }
}

internal fun queryDisplayName(ctx: android.content.Context, uri: android.net.Uri): String? {
    try {
        ctx.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) return c.getString(idx)
            }
        }
    } catch (t: Throwable) {}
    return null
}

internal fun queryRealPath(ctx: android.content.Context, uri: android.net.Uri): String? {
    try {
        if (uri.scheme == "file") return uri.path
        val proj = arrayOf(android.provider.MediaStore.Files.FileColumns.DATA)
        ctx.contentResolver.query(uri, proj, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(android.provider.MediaStore.Files.FileColumns.DATA)
                if (idx >= 0) {
                    val p = c.getString(idx)
                    if (!p.isNullOrBlank()) return p
                }
            }
        }
    } catch (t: Throwable) {}
    try {
        val id = android.provider.DocumentsContract.getDocumentId(uri)
        if (id.startsWith("raw:")) return id.removePrefix("raw:")
        if (id.contains(":")) {
            val p = id.substringAfter(":")
            if (p.startsWith("/")) return p
            return p
        }
    } catch (t: Throwable) {}
    return null
}

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
    "#  能量守恒",
    "#  步骤：1. 识别机型写小鱼儿密钥第6行  2. 适配机型先移除温控",
    "# ============================================================",
    "",
    "LOG=/storage/emulated/0/能量守恒日志.txt",
    "RULES_FILE=/data/adb/per_app_rules.txt",
    "",
    "# 规则文件不存在时自动写入（内嵌，方便打包进 APK）",
    "if [ ! -f \"\$RULES_FILE\" ]; then",
    "  cat > \"\$RULES_FILE\" <<'RULES_EOF'",
    "# ============================================================",
    "#  线程绑核规则",
    "#  格式：包名=核        或   包名{线程名}=核",
    "#  核：e-core / p-core / hp-core / p-core,hp-core",
    "#  # 开头为注释",
    "# ============================================================",
    "",
    "# ---------- 腾讯 ----------",
    "com.tencent.tmgp.sgame=hp-core",
    "com.tencent.tmgp.sgamece=hp-core",
    "com.tencent.tmgp.dfm=p-core,hp-core",
    "# 失控进化（Unity 引擎）：主线程 UnityMain、渲染 UnityGfxDeviceW",
    "com.tencent.rmcn=hp-core",
    "com.tencent.rmcn{UnityMain}=hp-core",
    "com.tencent.rmcn{UnityGfxDeviceW}=hp-core",
    "com.tencent.rmcn{IL2CPP Threadpo}=p-core",
    "com.tencent.rmcn{CodecLooper}=e-core",
    "com.tencent.rmcn{NDK MediaCodec_}=e-core",
    "com.tencent.rmcn{AssetGarbageCol}=e-core",
    "com.tencent.rmcn{Background Job.}=e-core",
    "com.tencent.tmgp.pubgmhd{RenderThread}=hp-core",
    "com.tencent.tmgp.pubgmhd=hp-core",
    "# PUBG Mobile 各服",
    "com.tencent.ig=hp-core",
    "com.pubg.imobile=hp-core",
    "com.pubg.krmobile=hp-core",
    "com.rekoo.pubgm=hp-core",
    "com.vng.pubgmobile=hp-core",
    "com.pubg.newstate=hp-core",
    "com.tencent.lolm=hp-core",
    "com.tencent.tmgp.cf=hp-core",
    "com.tencent.tmgp.cod=p-core,hp-core",
    "com.tencent.mf.uam=p-core,hp-core",
    "com.tencent.tmgp.nzfuture=p-core,hp-core",
    "",
    "# ---------- 米哈游 ----------",
    "com.miHoYo.Yuanshen=p-core,hp-core",
    "com.miHoYo.GenshinImpact=p-core,hp-core",
    "com.miHoYo.hkrpg=p-core,hp-core",
    "com.miHoYo.Nap=p-core,hp-core",
    "com.miHoYo.bh3=p-core,hp-core",
    "",
    "# ---------- 网易 ----------",
    "com.netease.yjwujian=p-core,hp-core",
    "com.netease.tieshu=p-core,hp-core",
    "com.netease.mrzh=hp-core",
    "com.netease.dwrg=hp-core",
    "com.netease.mc.mi=hp-core",
    "",
    "# ---------- 其他 ----------",
    "com.kurogame.mingchao=p-core,hp-core",
    "com.kurogame.haru=p-core,hp-core",
    "com.papegames.wutheringwaves=p-core,hp-core",
    "# 无畏契约（Valorant）",
    "com.tencent.tmgp.valorant=hp-core",
    "com.riotgames.valorant=hp-core",
    "com.riotgames.league.wildrift=hp-core",
    "com.supercell.brawlstars=hp-core",
    "com.supercell.clashofclans=e-core,p-core",
    "com.mojang.minecraftpe=hp-core",
    "com.pwrd.hotta=hp-core",
    "RULES_EOF",
    "  chmod 644 \"\$RULES_FILE\" 2>/dev/null",
    "fi",
    "MODEL=\$(getprop ro.product.model 2>/dev/null)",
    "SOC=\$(getprop ro.board.platform 2>/dev/null)",
    "",
    "# ---------- 1. 机型识别 ----------",
    "NAME=\"暂未适配此机型\"",
    "case \"\$MODEL\" in",
    "  PLF110|PKG110)                    NAME=\"一加 Ace5 竞速版\" ;;",
    "  25097RP43C|25097RP4C|25097RP4D)  NAME=\"小米 Pad8\" ;;",
    "  25091RP04C|25091RP04|25091RP0)   NAME=\"小米 Pad8 Pro\" ;;",
    "  25079RPDCC|25079RPDC)            NAME=\"红米 K Pad\" ;;",
    "  23046RP50C|25046RP50C|25046RP5)  NAME=\"小米 Pad6 Pro\" ;;",
    "esac",
    "",
    "KEY=/storage/emulated/0/小鱼儿/小鱼儿密钥",
    "mkdir -p \"\$(dirname \"\$KEY\")\" 2>/dev/null",
    "if [ -f \"\$KEY\" ]; then",
    "  awk -v n=6 -v v=\"\$NAME\" '",
    "    NR==n { print v; done=1; next }",
    "    { print }",
    "    END { if (!done) for (i=NR+1; i<=n; i++) print (i==n ? v : \"\") }",
    "  ' \"\$KEY\" > \"\$KEY.tmp\" 2>/dev/null && mv -f \"\$KEY.tmp\" \"\$KEY\"",
    "else",
    "  printf '\\n\\n\\n\\n\\n%s\\n' \"\$NAME\" > \"\$KEY\" 2>/dev/null",
    "fi",
    "chmod 600 \"\$KEY\" 2>/dev/null",
    "",
    "echo \"机型: \$MODEL (\$SOC) -> \$NAME\"",
    "",
    "# ---------- 机型参数（CPU min 百分比 / 刷新率 / GPU 类型）----------",
    "CPU_MIN_PCT=30",
    "HZ_TARGET=0",
    "GPU_TYPE=\"auto\"",
    "case \"\$NAME\" in",
    "  \"一加 Ace5 竞速版\")",
    "    CPU_MIN_PCT=30          # MTK sugov_ext 焊不住太低，给 30%",
    "    HZ_TARGET=120",
    "    GPU_TYPE=\"mtk\" ;;",
    "  \"小米 Pad8 Pro\"|\"小米 Pad8\")",
    "    CPU_MIN_PCT=33          # 8 Elite/8Gen4，文档实测 1152000/3532800≈32.6%",
    "    HZ_TARGET=144",
    "    GPU_TYPE=\"kgsl\" ;;",
    "  \"红米 K Pad\")",
    "    CPU_MIN_PCT=30",
    "    HZ_TARGET=165",
    "    GPU_TYPE=\"mtk\" ;;",
    "  \"小米 Pad6 Pro\")",
    "    CPU_MIN_PCT=30",
    "    HZ_TARGET=144",
    "    GPU_TYPE=\"kgsl\" ;;",
    "esac",
    "# GPU 类型自动兜底",
    "if [ \"\$GPU_TYPE\" = \"auto\" ]; then",
    "  if [ -d /sys/class/kgsl/kgsl-3d0 ]; then GPU_TYPE=\"kgsl\"",
    "  elif [ -f /proc/gpufreqv2/fix_target_opp_index ]; then GPU_TYPE=\"mtk\"",
    "  fi",
    "fi",
    "echo \"参数: CPU下限=\${CPU_MIN_PCT}%  刷新率=\${HZ_TARGET}Hz  GPU=\${GPU_TYPE}\"",
    "echo \"\$(date '+%H:%M:%S') 启动 机型=\$NAME 目标刷新率=\$HZ_TARGET 当前=\$(settings get system peak_refresh_rate 2>/dev/null)\" >> \"\$LOG\"",
    "",
    "# 未适配机型：只写密钥，不做任何修改",
    "case \"\$NAME\" in",
    "  \"暂未适配此机型\")",
    "    echo \"未适配机型，不执行任何修改\"",
    "    exit 0 ;;",
    "esac",
    "",
    "# ---------- 2. 冻结系统游戏助手/管家/云控 ----------",
    "# 单个包：禁用 + 强制停止 + 杀进程（冻结期间不允许它做任何事）",
    "freeze_one() {",
    "  _p=\"\$1\"",
    "  _base=\"\${_p%%:*}\"          # 去掉 :ui / :cache，得到基础包名",
    "  # 1. 深度禁用：直接 pm hide（小米对 disable-user 有保护，hide 才有效）",
    "  pm hide --user 0 \"\$_base\" >/dev/null 2>&1 || pm hide \"\$_base\" >/dev/null 2>&1",
    "  # 同时补 disable-user（部分系统 hide 后仍可 disable）",
    "  pm disable-user --user 0 \"\$_base\" >/dev/null 2>&1",
    "  # 校验：还在列表里说明 hide 成功（hidden 的包不显示在 pm list packages）",
    "  _vis=\$(pm list packages \"\$_base\" 2>/dev/null | grep -c \"package:\$_base\$\")",
    "  [ \"\$_vis\" = \"0\" ] && echo \"  \$_base 已隐藏\" || echo \"  \$_base 隐藏失败\"",
    "  # 2. 强制停止",
    "  am force-stop \"\$_base\" >/dev/null 2>&1",
    "  # 3. 杀掉包下所有进程（含 :ui / :cache 子进程）",
    "  for _pid in \$(pidof \"\$_base\" 2>/dev/null) \\",
    "              \$(ps -A -o PID,ARGS 2>/dev/null | grep \"[c]\${_base}\" | awk '{print \$1}'); do",
    "    [ -n \"\$_pid\" ] && kill -9 \"\$_pid\" 2>/dev/null",
    "  done",
    "  # 4. 清后台任务栈",
    "  cmd activity stop-app \"\$_base\" >/dev/null 2>&1",
    "  echo \"\$_base\" >> /data/adb/nc_frozen 2>/dev/null",
    "  echo \"  深度禁用: \$_p\"",
    "}",
    "",
    "freeze_pkgs() {",
    "  : > /data/adb/nc_frozen 2>/dev/null",
    "  BRAND=\$(getprop ro.product.brand 2>/dev/null)",
    "  MANU=\$(getprop ro.product.manufacturer 2>/dev/null)",
    "  echo \"品牌: \$BRAND / \$MANU\"",
    "",
    "  case \"\$BRAND\$MANU\" in",
    "    *OnePlus*|*oneplus*|*OPPO*|*oppo*|*realme*|*oplus*|*Oplus*)",
    "      # 绿厂：游戏助手 / 游戏空间 / 游戏中心",
    "      for p in com.oplus.games com.heytap.gamespace com.nearme.gamecenter; do",
    "        freeze_one \"\$p\"",
    "      done ;;",
    "    *Xiaomi*|*xiaomi*|*Redmi*|*redmi*|*POCO*|*poco*|*Xiaomi*)",
    "      # 小米：云控 joyose + 安全中心的 ui/cache 子进程",
    "      for p in com.xiaomi.joyose \\",
    "               com.miui.securitycenter:ui com.miui.securitycenter:cache; do",
    "        freeze_one \"\$p\"",
    "      done ;;",
    "    *)",
    "      echo \"  未识别的系统，跳过冻结\" ;;",
    "  esac",
    "}",
    "",
    "",
    "# 解冻：只恢复记录里的包（我们冻结的那几个）",
    "unfreeze_pkgs() {",
    "  if [ -f /data/adb/nc_frozen ]; then",
    "    while read -r _p; do",
    "      [ -n \"\$_p\" ] || continue",
    "      pm enable --user 0 \"\$_p\" >/dev/null 2>&1",
    "      pm unhide --user 0 \"\$_p\" >/dev/null 2>&1 || pm unhide \"\$_p\" >/dev/null 2>&1",
    "      echo \"  恢复: \$_p\"",
    "    done < /data/adb/nc_frozen",
    "    rm -f /data/adb/nc_frozen 2>/dev/null",
    "  fi",
    "}",
    "",
    "# ---------- 3. 移除温控 ----------",
    "remove_thermal() {",
    "  echo \"移除温控...\"",
    "",
    "  # 3.1 停 init 服务（停掉后不会被自动重启）",
    "  for s in \$(getprop 2>/dev/null | sed -n 's/^\\[init\\.svc\\.\\([^]]*\\)\\]: \\[running\\]/\\1/p' | grep -Ei 'therm'); do",
    "    stop \"\$s\" 2>/dev/null && echo \"  停止服务: \$s\"",
    "  done",
    "",
    "  # 3.2 杀残留进程",
    "  killall thermald mi_thermald thermal-engine thermal-engine-64 thermalserviced 2>/dev/null",
    "",
    "  # 3.3 关闭所有热区模式",
    "  for z in /sys/class/thermal/thermal_zone*/; do",
    "    [ -d \"\$z\" ] || continue",
    "    chmod 0666 \"\$z/mode\" 2>/dev/null",
    "    echo disabled > \"\$z/mode\" 2>/dev/null",
    "  done",
    "",
    "  # 3.4 关闭 cooling device",
    "  for cd in /sys/class/thermal/cooling_device*/cur_state; do",
    "    [ -f \"\$cd\" ] && { chmod 0666 \"\$cd\" 2>/dev/null; echo 0 > \"\$cd\" 2>/dev/null; }",
    "  done",
    "",
    "  # 3.5 覆盖漏网温控配置（bind 空文件）",
    "  : > /data/local/tmp/.nc_empty",
    "  for f in /vendor/etc/thermal/* /vendor/etc/*thermal* /system/etc/*thermal* /vendor/etc/.tp/*; do",
    "    [ -f \"\$f\" ] || continue",
    "    mount --bind /data/local/tmp/.nc_empty \"\$f\" 2>/dev/null",
    "  done",
    "",
    "  echo \"温控已移除\"",
    "}",
    "",
    "# restore 模式跳过拉满（避免先开再关）",
    "case \"\$1\" in",
    "  restore|--restore) ;;",
    "  *) remove_thermal ;;",
    "esac",
    "",
    "# ---------- 4. 性能拉满（文档方法，不含温控）----------",
    "push_perf() {",
    "  echo \"应用性能参数...\"",
    "",
    "  # 4.1 perfmgr：1 = 应用启动瞬间给 boost（骁龙专属）",
    "  if [ -f /sys/module/perfmgr/parameters/perfmgr_enable ]; then",
    "    chmod 0666 /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null",
    "    echo 1 > /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null && echo \"  perfmgr_enable=1\"",
    "  fi",
    "",
    "  # 4.2 load_scaling_y 修正为 1（会异常变成 -1）",
    "  if [ -f /sys/module/perfmgr/parameters/load_scaling_y ]; then",
    "    chmod 0666 /sys/module/perfmgr/parameters/load_scaling_y 2>/dev/null",
    "    echo 1 > /sys/module/perfmgr/parameters/load_scaling_y 2>/dev/null && echo \"  load_scaling_y=1\"",
    "  fi",
    "",
    "  # 4.3 GPU：按机型类型处理",
    "  case \"\$GPU_TYPE\" in",
    "    kgsl)",
    "      K=/sys/class/kgsl/kgsl-3d0",
    "      _mx=\$(cat \"\$K/devfreq/max_freq\" 2>/dev/null)",
    "      if [ -n \"\$_mx\" ]; then",
    "        chmod 0666 \"\$K/devfreq/min_freq\" \"\$K/devfreq/max_freq\" 2>/dev/null",
    "        echo \"\$_mx\" > \"\$K/devfreq/min_freq\" 2>/dev/null",
    "        echo \"\$_mx\" > \"\$K/devfreq/max_freq\" 2>/dev/null",
    "        echo \"  GPU(kgsl) 锁定 \$_mx\"",
    "      fi ;;",
    "    mtk)",
    "      if [ -f /proc/gpufreqv2/fix_target_opp_index ]; then",
    "        chmod 0666 /proc/gpufreqv2/fix_target_opp_index 2>/dev/null",
    "        echo 0 > /proc/gpufreqv2/fix_target_opp_index 2>/dev/null",
    "        echo \"  GPU(MTK) 目标 OPP 0\"",
    "      fi ;;",
    "  esac",
    "",
    "  # 4.4 CPU：max = 硬件最高，min 抬高到 35%（减少爬坡）",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null)",
    "    [ -n \"\$hw\" ] || continue",
    "    mn=\$((hw * CPU_MIN_PCT / 100))",
    "    chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" 2>/dev/null",
    "    echo \"\$hw\" > \"\$p/scaling_max_freq\" 2>/dev/null",
    "    echo \"\$mn\" > \"\$p/scaling_min_freq\" 2>/dev/null",
    "    echo \"  \$(basename \"\$p\"): max=\$hw min=\$mn\"",
    "  done",
    "",
    "  # 4.5 刷新率：优先机型目标值，没有则取面板最高",
    "  F=\"\$HZ_TARGET\"",
    "  case \"\$F\" in ''|0|*[!0-9]*) F=\$(dumpsys display 2>/dev/null | grep -oE 'fps=[0-9.]+' | grep -oE '[0-9]+' | sort -n | tail -1) ;; esac",
    "  case \"\$F\" in ''|null|*[!0-9]*) F=120 ;; esac",
    "  [ \"\$F\" -gt 0 ] 2>/dev/null || F=120",
    "  settings put system peak_refresh_rate \"\$F\" 2>/dev/null",
    "  settings put system min_refresh_rate \"\$F\" 2>/dev/null",
    "  echo \"  刷新率 \$F\"",
    "",
    "  echo \"性能参数已应用\"",
    "}",
    "",
    "case \"\$1\" in",
    "  restore|--restore) ;;",
    "  *) push_perf ;;",
    "esac",
    "",
    "# ---------- 6. 前台轮询：游戏=高性能 / 非游戏=降温省电 ----------",
    "# ---------- 消除周期性卡顿：禁用 CPU 深睡 ----------",
    "idle_deep_off() {",
    "  for c in /sys/devices/system/cpu/cpu[0-9]*; do",
    "    for s in \"\$c\"/cpuidle/state*; do",
    "      [ -f \"\$s/latency\" ] || continue",
    "      _lat=\$(cat \"\$s/latency\" 2>/dev/null)",
    "      case \"\$_lat\" in ''|*[!0-9]*) continue ;; esac",
    "      # 延迟 > 400us 的深度状态禁掉（浅睡 WFI 保留）",
    "      if [ \"\$_lat\" -gt 400 ]; then",
    "        chmod 0666 \"\$s/disable\" 2>/dev/null",
    "        echo 1 > \"\$s/disable\" 2>/dev/null",
    "      fi",
    "    done",
    "  done",
    "}",
    "",
    "idle_deep_on() {",
    "  for c in /sys/devices/system/cpu/cpu[0-9]*; do",
    "    for s in \"\$c\"/cpuidle/state*; do",
    "      [ -f \"\$s/disable\" ] || continue",
    "      chmod 0666 \"\$s/disable\" 2>/dev/null",
    "      echo 0 > \"\$s/disable\" 2>/dev/null",
    "    done",
    "  done",
    "}",
    "",
    "# ---------- 锁高内存带宽（DDR/L3/LLCC）----------",
    "ddr_lock_high() {",
    "  # MTK：devfreq 里的 dvfsrc",
    "  for d in /sys/class/devfreq/*/; do",
    "    _n=\$(basename \"\$d\")",
    "    case \"\$_n\" in",
    "      *ddr*|*DDR*|*dvfsrc*|*l3*|*llcc*|*bus*)",
    "        _mx=\$(cat \"\$d/max_freq\" 2>/dev/null)",
    "        [ -n \"\$_mx\" ] || continue",
    "        chmod 0666 \"\$d/max_freq\" \"\$d/min_freq\" \"\$d/governor\" 2>/dev/null",
    "        echo userspace > \"\$d/governor\" 2>/dev/null",
    "        echo \"\$_mx\" > \"\$d/min_freq\" 2>/dev/null",
    "        echo \"\$_mx\" > \"\$d/max_freq\" 2>/dev/null",
    "        echo \"  DDR 锁高(devfreq): \$_n -> \$_mx\" ;;",
    "    esac",
    "  done",
    "  # 高通：/sys/devices/system/cpu/bus_dcvs/ 下的 DDR 与 LLCC",
    "  for d in /sys/devices/system/cpu/bus_dcvs/DDR/*/ /sys/devices/system/cpu/bus_dcvs/LLCC/*/; do",
    "    [ -d \"\$d\" ] || continue",
    "    _mx=\$(cat \"\$d/max_freq\" 2>/dev/null)",
    "    [ -n \"\$_mx\" ] || continue",
    "    chmod 0666 \"\$d/max_freq\" \"\$d/min_freq\" 2>/dev/null",
    "    echo \"\$_mx\" > \"\$d/min_freq\" 2>/dev/null",
    "    echo \"  DDR/LLCC 锁高(bus_dcvs): \$(basename \"\$d\") -> \$_mx\"",
    "  done",
    "}",
    "",
    "ddr_release() {",
    "  for d in /sys/class/devfreq/*/; do",
    "    _n=\$(basename \"\$d\")",
    "    case \"\$_n\" in",
    "      *ddr*|*DDR*|*dvfsrc*|*l3*|*llcc*|*bus*)",
    "        _mn=\$(cat \"\$d/available_frequencies\" 2>/dev/null | tr ' ' '\\n' | sort -n | head -1)",
    "        chmod 0666 \"\$d/min_freq\" \"\$d/governor\" 2>/dev/null",
    "        [ -n \"\$_mn\" ] && echo \"\$_mn\" > \"\$d/min_freq\" 2>/dev/null",
    "        echo simple_ondemand > \"\$d/governor\" 2>/dev/null ;;",
    "    esac",
    "  done",
    "  # 高通 bus_dcvs 恢复：min_freq 放回最低",
    "  for d in /sys/devices/system/cpu/bus_dcvs/DDR/*/ /sys/devices/system/cpu/bus_dcvs/LLCC/*/; do",
    "    [ -d \"\$d\" ] || continue",
    "    _mn=\$(cat \"\$d/min_freq\" 2>/dev/null)",
    "    [ -n \"\$_mn\" ] || continue",
    "    chmod 0666 \"\$d/min_freq\" 2>/dev/null",
    "    # 用 freq_map 的最低档",
    "    _low=\$(cat \"\$d/freq_map\" 2>/dev/null | tr ' ' '\\n' | sort -n | head -1)",
    "    [ -n \"\$_low\" ] && echo \"\$_low\" > \"\$d/min_freq\" 2>/dev/null",
    "  done",
    "}",
    "",
    "apply_game_mode() {",
    "  # 冻结游戏助手/管家/云控",
    "  freeze_pkgs",
    "  # CPU 满频 + 下限抬高",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" 2>/dev/null",
    "    echo \"\$hw\" > \"\$p/scaling_max_freq\" 2>/dev/null",
    "    echo \$((hw * CPU_MIN_PCT / 100)) > \"\$p/scaling_min_freq\" 2>/dev/null",
    "  done",
    "  # GPU 锁满（min 和 max 都要写）",
    "  K=/sys/class/kgsl/kgsl-3d0",
    "  if [ -d \"\$K\" ]; then",
    "    _mx=\$(cat \"\$K/devfreq/max_freq\" 2>/dev/null)",
    "    if [ -n \"\$_mx\" ]; then",
    "      chmod 0666 \"\$K/devfreq/min_freq\" \"\$K/devfreq/max_freq\" 2>/dev/null",
    "      echo \"\$_mx\" > \"\$K/devfreq/min_freq\" 2>/dev/null",
    "      echo \"\$_mx\" > \"\$K/devfreq/max_freq\" 2>/dev/null",
    "      [ -f \"\$K/devfreq/mod_percent\" ] && { chmod 0666 \"\$K/devfreq/mod_percent\" 2>/dev/null; echo 110 > \"\$K/devfreq/mod_percent\" 2>/dev/null; }",
    "      # pwrlevel 才是 msm-adreno-tz 真正认的接口：min=max=0 锁最高档",
    "      [ -f \"\$K/min_pwrlevel\" ] && { chmod 0666 \"\$K/min_pwrlevel\" 2>/dev/null; echo 0 > \"\$K/min_pwrlevel\" 2>/dev/null; }",
    "      [ -f \"\$K/max_pwrlevel\" ] && { chmod 0666 \"\$K/max_pwrlevel\" 2>/dev/null; echo 0 > \"\$K/max_pwrlevel\" 2>/dev/null; }",
    "    fi",
    "  fi",
    "  # 禁用 CPU 深睡（消除周期性卡顿）",
    "  idle_deep_off",
    "  # 锁高内存带宽",
    "  ddr_lock_high",
    "  # core_ctl 锁核：min_cpus = max_cpus（防止系统关核）",
    "  for d in /sys/devices/system/cpu/cpu*/core_ctl; do",
    "    [ -d \"\$d\" ] || continue",
    "    _mx=\$(cat \"\$d/max_cpus\" 2>/dev/null)",
    "    if [ -n \"\$_mx\" ]; then",
    "      chmod 0666 \"\$d/min_cpus\" 2>/dev/null",
    "      echo \"\$_mx\" > \"\$d/min_cpus\" 2>/dev/null",
    "    fi",
    "  done",
    "  # 温控关闭",
    "  for z in /sys/class/thermal/thermal_zone*/mode; do",
    "    [ -f \"\$z\" ] && { chmod 0666 \"\$z\" 2>/dev/null; echo disabled > \"\$z\" 2>/dev/null; }",
    "  done",
    "  # 刷新率恢复机型目标（peak 和 min 都要检查，只比 peak 会漏掉 min=60 的情况）",
    "  if [ \"\$HZ_TARGET\" -gt 0 ] 2>/dev/null; then",
    "    _cur=\$(settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
    "    _curmin=\$(settings get system min_refresh_rate 2>/dev/null | cut -d. -f1)",
    "    # 切档时无条件写一次（system + user 0 双写，兼容 HyperOS）",
    "    settings put system peak_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    settings put system min_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    settings put system user_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    settings --user 0 put system peak_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    settings --user 0 put system min_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    settings --user 0 put system user_refresh_rate \"\$HZ_TARGET\" 2>/dev/null",
    "    # 读回验证",
    "    _after=\$(settings get system peak_refresh_rate 2>/dev/null)",
    "    echo \"\$(date '+%H:%M:%S') 刷新率 -> \$HZ_TARGET（写前 peak=\$_cur min=\$_curmin，写后 peak=\$_after）\" >> \"\$LOG\"",
    "  fi",
    "}",
    "",
    "apply_power_mode() {",
    "  # 恢复被冻结的包",
    "  unfreeze_pkgs",
    "  # CPU 上限 50%，下限放开",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    mn=\$(cat \"\$p/cpuinfo_min_freq\" 2>/dev/null)",
    "    chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" 2>/dev/null",
    "    echo \$((hw * 50 / 100)) > \"\$p/scaling_max_freq\" 2>/dev/null",
    "    [ -n \"\$mn\" ] && echo \"\$mn\" > \"\$p/scaling_min_freq\" 2>/dev/null",
    "  done",
    "  # GPU 恢复动态：min_pwrlevel 放回最低档，允许降频",
    "  K=/sys/class/kgsl/kgsl-3d0",
    "  if [ -d \"\$K\" ]; then",
    "    _pn=\$(cat \"\$K/num_pwrlevels\" 2>/dev/null)",
    "    _mn=\$(cat \"\$K/devfreq/available_frequencies\" 2>/dev/null | tr ' ' '\\n' | sort -n | head -1)",
    "    chmod 0666 \"\$K/devfreq/min_freq\" 2>/dev/null",
    "    [ -n \"\$_mn\" ] && echo \"\$_mn\" > \"\$K/devfreq/min_freq\" 2>/dev/null",
    "    if [ -n \"\$_pn\" ]; then",
    "      chmod 0666 \"\$K/min_pwrlevel\" 2>/dev/null",
    "      echo \$((_pn - 1)) > \"\$K/min_pwrlevel\" 2>/dev/null",
    "    fi",
    "  fi",
    "  # 恢复 CPU 深睡",
    "  idle_deep_on",
    "  # 释放内存带宽",
    "  ddr_release",
    "  # core_ctl 恢复 min=0（允许省电关核）",
    "  for d in /sys/devices/system/cpu/cpu*/core_ctl; do",
    "    [ -d \"\$d\" ] || continue",
    "    chmod 0666 \"\$d/min_cpus\" 2>/dev/null",
    "    echo 0 > \"\$d/min_cpus\" 2>/dev/null",
    "  done",
    "  # 温控恢复启用（降温）",
    "  for z in /sys/class/thermal/thermal_zone*/mode; do",
    "    [ -f \"\$z\" ] && { chmod 0666 \"\$z\" 2>/dev/null; echo enabled > \"\$z\" 2>/dev/null; }",
    "  done",
    "  # 日常降刷新率到 60（省电）",
    "  _cur=\$(settings get system peak_refresh_rate 2>/dev/null | cut -d. -f1)",
    "  if [ \"\$_cur\" != \"60\" ]; then",
    "    settings put system peak_refresh_rate 60 2>/dev/null",
    "    settings put system min_refresh_rate 60 2>/dev/null",
    "    settings put system user_refresh_rate 60 2>/dev/null",
    "    echo \"\$(date '+%H:%M:%S') 刷新率 -> 60（原 \$_cur）\" >> \"\$LOG\"",
    "  fi",
    "}",
    "",
    "# ---------- 防覆盖：每轮校验并修正被改回的值 ----------",
    "verify_game() {",
    "  # CPU max + min",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null); [ -n \"\$hw\" ] || continue",
    "    want_min=\$((hw * CPU_MIN_PCT / 100))",
    "    cur_max=\$(cat \"\$p/scaling_max_freq\" 2>/dev/null)",
    "    cur_min=\$(cat \"\$p/scaling_min_freq\" 2>/dev/null)",
    "    [ \"\$cur_max\" = \"\$hw\" ] || { chmod 0666 \"\$p/scaling_max_freq\" 2>/dev/null; echo \"\$hw\" > \"\$p/scaling_max_freq\" 2>/dev/null; }",
    "    [ \"\$cur_min\" = \"\$want_min\" ] || { chmod 0666 \"\$p/scaling_min_freq\" 2>/dev/null; echo \"\$want_min\" > \"\$p/scaling_min_freq\" 2>/dev/null; }",
    "  done",
    "  # GPU min_freq（只校验 min，max 一般不会被压）",
    "  K=/sys/class/kgsl/kgsl-3d0",
    "  if [ -d \"\$K\" ]; then",
    "    mx=\$(cat \"\$K/devfreq/max_freq\" 2>/dev/null)",
    "    mn=\$(cat \"\$K/devfreq/min_freq\" 2>/dev/null)",
    "    if [ -n \"\$mx\" ] && [ \"\$mn\" != \"\$mx\" ]; then",
    "      chmod 0666 \"\$K/devfreq/min_freq\" 2>/dev/null",
    "      echo \"\$mx\" > \"\$K/devfreq/min_freq\" 2>/dev/null",
    "    fi",
    "  fi",
    "  # GPU pwrlevel（apply_game_mode 设的 min_pwrlevel=0，被改回就修）",
    "  if [ -d \"\$K\" ] && [ -f \"\$K/min_pwrlevel\" ]; then",
    "    _pv=\$(cat \"\$K/min_pwrlevel\" 2>/dev/null)",
    "    [ \"\$_pv\" = \"0\" ] || { chmod 0666 \"\$K/min_pwrlevel\" 2>/dev/null; echo 0 > \"\$K/min_pwrlevel\" 2>/dev/null; }",
    "  fi",
    "  # DDR/LLCC（高通 bus_dcvs）",
    "  for _d in /sys/devices/system/cpu/bus_dcvs/DDR/*/ /sys/devices/system/cpu/bus_dcvs/LLCC/*/; do",
    "    [ -d \"\$_d\" ] || continue",
    "    _m=\$(cat \"\$_d/max_freq\" 2>/dev/null)",
    "    _n=\$(cat \"\$_d/min_freq\" 2>/dev/null)",
    "    if [ -n \"\$_m\" ] && [ \"\$_n\" != \"\$_m\" ]; then",
    "      chmod 0666 \"\$_d/min_freq\" 2>/dev/null",
    "      echo \"\$_m\" > \"\$_d/min_freq\" 2>/dev/null",
    "    fi",
    "  done",
    "  # DDR（MTK dvfsrc）",
    "  if [ -d /sys/class/devfreq/mtk-dvfsrc-devfreq ]; then",
    "    _m=\$(cat /sys/class/devfreq/mtk-dvfsrc-devfreq/max_freq 2>/dev/null)",
    "    _n=\$(cat /sys/class/devfreq/mtk-dvfsrc-devfreq/min_freq 2>/dev/null)",
    "    if [ -n \"\$_m\" ] && [ \"\$_n\" != \"\$_m\" ]; then",
    "      chmod 0666 /sys/class/devfreq/mtk-dvfsrc-devfreq/min_freq 2>/dev/null",
    "      echo \"\$_m\" > /sys/class/devfreq/mtk-dvfsrc-devfreq/min_freq 2>/dev/null",
    "    fi",
    "  fi",
    "  # 刷新率不在这里校验（每轮写 settings 会让 SurfaceFlinger 重配 → 周期性卡顿），只在切档时设",
    "  # perfmgr",
    "  [ -f /sys/module/perfmgr/parameters/perfmgr_enable ] && {",
    "    v=\$(cat /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null)",
    "    [ \"\$v\" = \"1\" ] || { chmod 0666 /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null; echo 1 > /sys/module/perfmgr/parameters/perfmgr_enable 2>/dev/null; }",
    "  }",
    "}",
    "",
    "BIND_ON=1   # 默认开启线程级绑核；传 -n 关闭",
    "",
    "# ---------- 线程级绑核（仿鱼蛋二改版，纯 shell）----------",
    "# 主线程 → hp-core（超大核）",
    "# 渲染/图形 → 性能核（p+h）",
    "# 音频/网络 → e-core（小核，不抢渲染）",
    "# 进程级规则：包名=核（只用于主线程）",
    "rule_main() {",
    "  [ -f \"\$RULES_FILE\" ] || return 1",
    "  _r=\$(grep -m1 \"^\$1=\" \"\$RULES_FILE\" 2>/dev/null | cut -d= -f2)",
    "  [ -n \"\$_r\" ] && { echo \"\$_r\"; return 0; }",
    "  return 1",
    "}",
    "",
    "# 线程级规则：包名{线程名}=核",
    "rule_thread() {",
    "  [ -f \"\$RULES_FILE\" ] || return 1",
    "  _r=\$(grep -m1 \"^\$1{\$2}=\" \"\$RULES_FILE\" 2>/dev/null | cut -d= -f2)",
    "  [ -n \"\$_r\" ] && { echo \"\$_r\"; return 0; }",
    "  return 1",
    "}",
    "",
    "# 语义核 -> hex 掩码",
    "sem_to_mask() {",
    "  _sm=0",
    "  for _ss in \$(echo \"\$1\" | tr ',' ' '); do",
    "    case \"\$_ss\" in",
    "      e-core)  _sm=\$((_sm | E_MASK)) ;;",
    "      p-core)  _sm=\$((_sm | P_BIND_MASK)) ;;",
    "      hp-core) _sm=\$((_sm | H_MASK)) ;;",
    "    esac",
    "  done",
    "  [ \"\$_sm\" -gt 0 ] && printf '%x' \"\$_sm\"",
    "}",
    "",
    "bind_threads_detail() {",
    "  _pkg=\"\$1\"",
    "  _pids=\$(pidof \"\$_pkg\" 2>/dev/null)",
    "  [ -n \"\$_pids\" ] || return 0",
    "",
    "  for _pid in \$_pids; do",
    "    [ -d \"/proc/\$_pid/task\" ] || continue",
    "",
    "    # 主线程（优先进程级规则）",
    "    _mr=\$(rule_main \"\$_pkg\")",
    "    _mm=\$(sem_to_mask \"\${_mr:-hp-core}\")",
    "    [ -n \"\$_mm\" ] && taskset -p \"0x\$_mm\" \"\$_pid\" 2>/dev/null",
    "",
    "    # 逐个线程按名字绑",
    "    for _t in /proc/\$_pid/task/*; do",
    "      _tid=\${_t##*/}",
    "      _nm=\$(cat \"\$_t/comm\" 2>/dev/null)",
    "      [ -n \"\$_nm\" ] || continue",
    "      # 子线程只认线程级规则（进程级规则只管主线程，避免音频/网络被绑大核）",
    "      _r=\$(rule_thread \"\$_pkg\" \"\$_nm\")",
    "      if [ -n \"\$_r\" ]; then",
    "        _m=\$(sem_to_mask \"\$_r\")",
    "        [ -n \"\$_m\" ] && taskset -p \"0x\$_m\" \"\$_tid\" 2>/dev/null",
    "        continue",
    "      fi",
    "      case \"\$_nm\" in",
    "        # 渲染/图形/游戏主线程 → 超大核",
    "        RenderThread|RHIThread|GameThread|UnityMain|UnityGfxDeviceW|RHIThread|TaskGraphNP*)",
    "          taskset -p \"0x\$(printf '%x' \$H_MASK)\" \"\$_tid\" 2>/dev/null ;;",
    "        # 音频/解码/网络/IO/GC/后台 → 小核（不抢渲染）",
    "        *Audio*|*audio*|*Codec*|*codec*|*MediaCodec*|*Net*|*net*|*IoThread*|*io_thread*|*GarbageCol*|*Background*|*Job.*)",
    "          taskset -p \"0x\$(printf '%x' \$E_MASK)\" \"\$_tid\" 2>/dev/null ;;",
    "        # 其他线程 → 不绑（保持系统默认调度，让大核留给渲染）",
    "        *)",
    "          ;;",
    "      esac",
    "    done",
    "  done",
    "}",
    "",
    "game_loop() {",
    "  # 拓扑分簇（一次）",
    "  _flist=\"\"",
    "  for c in /sys/devices/system/cpu/cpu[0-9]*/cpufreq/cpuinfo_max_freq; do",
    "    [ -f \"\$c\" ] || continue",
    "    _n=\$(echo \"\$c\" | sed 's|.*/cpu\\([0-9]*\\)/.*|\\1|')",
    "    _f=\$(cat \"\$c\" 2>/dev/null)",
    "    [ -n \"\$_f\" ] && _flist=\"\$_flist \$_f:\$_n\"",
    "  done",
    "  _low=\$(echo \"\$_flist\" | tr ' ' '\\n' | sed '/^\$/d' | cut -d: -f1 | sort -n | head -1)",
    "  _high=\$(echo \"\$_flist\" | tr ' ' '\\n' | sed '/^\$/d' | cut -d: -f1 | sort -n | tail -1)",
    "  E_MASK=0; P_MASK=0; H_MASK=0; P_BIND_MASK=0",
    "  for pair in \$_flist; do",
    "    _f=\${pair%%:*}; _n=\${pair##*:}",
    "    _bit=\$(( 1 << _n ))",
    "    if [ \"\$_f\" = \"\$_low\" ]; then E_MASK=\$(( E_MASK | _bit ))",
    "    elif [ \"\$_f\" = \"\$_high\" ]; then H_MASK=\$(( H_MASK | _bit ))",
    "    else P_MASK=\$(( P_MASK | _bit )); fi",
    "  done",
    "  # 2 档架构没有独立中核，P_MASK=0 时“其他线程”回退到小核组",
    "  P_BIND_MASK=\$P_MASK",
    "  [ \"\$P_BIND_MASK\" = \"0\" ] && P_BIND_MASK=\$E_MASK",
    "  # 按频率档数决定绑核范围：",
    "  #   3 档以上 → 排除最小核簇（小核拖慢渲染）",
    "  #   2 档     → 绑全核（8 Elite 的\"小核组\"其实是 6 个性能核，排掉只剩 2 核）",
    "  _cnt=\$(echo \"\$_flist\" | tr ' ' '\\n' | sed '/^\$/d' | cut -d: -f1 | sort -u | wc -l)",
    "  if [ \"\$_cnt\" -ge 3 ]; then",
    "    PERF_MASK=\$(printf '%x' \$(( P_MASK | H_MASK )))",
    "    echo \"拓扑: e=0x\$(printf '%x' \$E_MASK) p=0x\$(printf '%x' \$P_MASK) h=0x\$(printf '%x' \$H_MASK) | \${_cnt}档 -> 绑 P+H（排除小核）\"",
    "  else",
    "    PERF_MASK=\$(printf '%x' \$(( E_MASK | P_MASK | H_MASK )))",
    "    echo \"拓扑: e=0x\$(printf '%x' \$E_MASK) p=0x\$(printf '%x' \$P_MASK) h=0x\$(printf '%x' \$H_MASK) | \${_cnt}档 -> 绑全核\"",
    "  fi",
    "",
    "  _last=\"\"",
    "  _same=0",
    "  while true; do",
    "    _fg=\$(dumpsys activity activities 2>/dev/null | grep -m1 -E 'topResumedActivity|mResumedActivity' | sed -n 's/.*[[:space:]]\\([a-zA-Z][a-zA-Z0-9._]*\\)\\/[a-zA-Z0-9_.]*.*/\\1/p' | head -1)",
    "    # 防抖：空值跳过；前台连续 2 次（20 秒）相同才认，避免 dumpsys 抖动误切",
    "    [ -z \"\$_fg\" ] && { sleep 10; continue; }",
    "    if [ \"\$_fg\" != \"\$_last\" ]; then",
    "      _same=\$((_same + 1))",
    "      if [ \"\$_same\" -lt 2 ]; then sleep 10; continue; fi",
    "      _same=0",
    "    fi",
    "    if [ -n \"\$_fg\" ] && [ \"\$_fg\" != \"\$_last\" ]; then",
    "      case \"\$_fg\" in",
    "        # 先排除腾讯的社交/工具类（极道帝兵 com.tencent.THY 不排除，按游戏处理）",
    "        com.tencent.mm|com.tencent.mobileqq|com.tencent.tim|com.tencent.wework|com.tencent.qqmusic|com.tencent.qqlive|com.tencent.news|com.tencent.qqmail|com.tencent.weread)",
    "          if [ \"\$_mode\" != \"power\" ]; then",
    "            _mode=\"power\"",
    "            apply_power_mode",
    "          fi",
    "          echo \"\$(date '+%H:%M:%S') [日常] \$_fg（非游戏） 刷新率=60\" | tee -a \"\$LOG\" ;;",
    "        com.tencent.*|com.miHoYo.*|com.mihoyo.*|com.netease.*|com.mojang.*|com.kurogame.*|com.papegames.*|com.garena.*|com.supercell.*|com.riotgames.*|com.activision.*|com.ea.*|com.gameloft.*|com.pwrd.*|com.pubg.*|com.rekoo.*|com.vng.*|com.levelinfinite.*)",
    "          # 已经处于游戏模式时（游戏 A → 游戏 B），只重新绑核，不重做冻结/调频",
    "          if [ \"\$_mode\" != \"game\" ]; then",
    "            _mode=\"game\"",
    "            apply_game_mode",
    "            # 首次进游戏清一次后台（会卡约 1 秒，只在进游戏那一刻）",
    "            am kill-all >/dev/null 2>&1",
    "            echo \"\$(date '+%H:%M:%S') 已清后台\" >> \"\$LOG\"",
    "          fi",
    "          if [ \"\$BIND_ON\" = \"1\" ]; then",
    "            bind_threads_detail \"\$_fg\"",
    "          fi",
    "          echo \"\$(date '+%H:%M:%S') [游戏] \$_fg -> 满频+绑核 刷新率=\$HZ_TARGET\" | tee -a \"\$LOG\" ;;",
    "        *)",
    "          if [ \"\$_mode\" != \"power\" ]; then",
    "            _mode=\"power\"",
    "            apply_power_mode",
    "          fi",
    "          echo \"\$(date '+%H:%M:%S') [省电] \$_fg -> 50%频率+温控启用 刷新率=60\" | tee -a \"\$LOG\" ;;",
    "      esac",
    "      _last=\"\$_fg\"",
    "    fi",
    "    # 按模式校验，防止被系统改回",
    "    if [ \"\$_mode\" = \"game\" ] && [ -n \"\$_fg\" ]; then",
    "      verify_game",
    "      # 游戏内监控：温度 / CPU 频率 / GPU 频率",
    "      _t=\$(cat /sys/class/thermal/thermal_zone0/temp 2>/dev/null)",
    "      case \"\$_t\" in ''|*[!0-9]*) _t=0 ;; esac",
    "      _c=\$(cat /sys/devices/system/cpu/cpufreq/policy0/scaling_cur_freq 2>/dev/null)",
    "      _g=\$(cat /sys/class/kgsl/kgsl-3d0/devfreq/cur_freq 2>/dev/null)",
    "      _gb=\$(awk '{if(\$2>0) printf \"%.0f\", \$1*100/\$2; else print 0}' /sys/class/kgsl/kgsl-3d0/gpubusy 2>/dev/null)",
    "      echo \"\$(date '+%H:%M:%S') [监控] 温度=\$((_t/1000))℃ CPU=\$_c GPU=\$_g GPU负载=\${_gb:-0}%\" >> \"\$LOG\"",
    "    fi",
    "    # 桌面模式不做每轮校验（频繁写 settings 会导致 SurfaceFlinger 反复重配 → 卡死）",
    "    sleep 10",
    "  done",
    "}",
    "",
    "# 默认执行完自动进入守护（游戏/省电双档切换）",
    "#   -n --once    只执行一次，不驻留",
    "#   -B --nobind  驻留但不绑核",
    "# ---------- 完整恢复（APK 关闭时调用 restore）----------",
    "restore_all() {",
    "  echo \"开始恢复...\"",
    "  # 1. 解冻",
    "  unfreeze_pkgs",
    "  # 2. 温控恢复",
    "  idle_deep_on",
    "  for z in /sys/class/thermal/thermal_zone*/mode; do",
    "    [ -f \"\$z\" ] && { chmod 0666 \"\$z\" 2>/dev/null; echo enabled > \"\$z\" 2>/dev/null; }",
    "  done",
    "  for z in /sys/class/thermal/thermal_zone*/emul_temp; do",
    "    [ -f \"\$z\" ] && { chmod 0666 \"\$z\" 2>/dev/null; echo 0 > \"\$z\" 2>/dev/null; }",
    "  done",
    "  for f in /vendor/etc/thermal/* /vendor/etc/*thermal* /system/etc/*thermal* /vendor/etc/.tp/*; do",
    "    [ -f \"\$f\" ] || continue",
    "    while umount \"\$f\" 2>/dev/null; do :; done",
    "  done",
    "  for sv in thermald mi_thermald thermal-engine thermalserviced; do",
    "    start \"\$sv\" 2>/dev/null",
    "  done",
    "  # 3. CPU 恢复默认",
    "  for p in /sys/devices/system/cpu/cpufreq/policy[0-9]*/; do",
    "    [ -d \"\$p\" ] || continue",
    "    hw=\$(cat \"\$p/cpuinfo_max_freq\" 2>/dev/null)",
    "    mn=\$(cat \"\$p/cpuinfo_min_freq\" 2>/dev/null)",
    "    chmod 0666 \"\$p/scaling_max_freq\" \"\$p/scaling_min_freq\" 2>/dev/null",
    "    [ -n \"\$hw\" ] && echo \"\$hw\" > \"\$p/scaling_max_freq\" 2>/dev/null",
    "    [ -n \"\$mn\" ] && echo \"\$mn\" > \"\$p/scaling_min_freq\" 2>/dev/null",
    "  done",
    "  # 4. GPU 恢复",
    "  K=/sys/class/kgsl/kgsl-3d0",
    "  if [ -d \"\$K\" ]; then",
    "    _pn=\$(cat \"\$K/num_pwrlevels\" 2>/dev/null)",
    "    [ -n \"\$_pn\" ] && { chmod 0666 \"\$K/min_pwrlevel\" 2>/dev/null; echo \$((_pn-1)) > \"\$K/min_pwrlevel\" 2>/dev/null; }",
    "    _mn=\$(cat \"\$K/devfreq/available_frequencies\" 2>/dev/null | tr ' ' '\\n' | sort -n | head -1)",
    "    [ -n \"\$_mn\" ] && { chmod 0666 \"\$K/devfreq/min_freq\" 2>/dev/null; echo \"\$_mn\" > \"\$K/devfreq/min_freq\" 2>/dev/null; }",
    "    [ -f \"\$K/devfreq/mod_percent\" ] && echo 100 > \"\$K/devfreq/mod_percent\" 2>/dev/null",
    "  fi",
    "  [ -f /proc/gpufreqv2/fix_target_opp_index ] && echo -1 > /proc/gpufreqv2/fix_target_opp_index 2>/dev/null",
    "  # 5. DDR 释放",
    "  ddr_release",
    "  # 6. core_ctl 恢复",
    "  for d in /sys/devices/system/cpu/cpu*/core_ctl; do",
    "    [ -d \"\$d\" ] || continue",
    "    chmod 0666 \"\$d/min_cpus\" 2>/dev/null",
    "    echo 0 > \"\$d/min_cpus\" 2>/dev/null",
    "  done",
    "  # 7. 刷新率恢复为面板最高",
    "  _f=\$(dumpsys display 2>/dev/null | grep -oE 'fps=[0-9.]+' | grep -oE '[0-9]+' | sort -n | tail -1)",
    "  case \"\$_f\" in ''|*[!0-9]*) _f=120 ;; esac",
    "  settings put system peak_refresh_rate \"\$_f\" 2>/dev/null",
    "  settings put system min_refresh_rate \"\$_f\" 2>/dev/null",
    "  echo \"恢复完成（刷新率 \$_f）\"",
    "}",
    "",
    "case \"\$1\" in",
    "  restore|--restore)",
    "    restore_all",
    "    exit 0",
    "    ;;",
    "  -n|--once)",
    "    freeze_pkgs",
    "    ;;",
    "  -B|--nobind)",
    "    BIND_ON=0",
    "    echo \"线程优化: 已关闭\"",
    "    game_loop",
    "    ;;",
    "  *)",
    "    BIND_ON=1",
    "    echo \"线程优化: shell 线程级绑核（仿鱼蛋版）\"",
    "    game_loop",
    "    ;;",
    "esac"
)

/**
 * 悬浮窗帧率显示（类似 Scene 的 FPS 悬浮窗）
 *
 * 实现方式：
 *   1. WindowManager 加一个 TYPE_APPLICATION_OVERLAY 的 TextView
 *   2. 定时（1s）用 root 执行 dumpsys SurfaceFlinger --latency 读帧时间
 *   3. 算 FPS 更新 TextView
 */
class FpsOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var fpsView: TextView? = null
    private var executor: ScheduledExecutorService? = null
    private var activePkg: String = ""
    private var lastPkgRefresh: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundCompat()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        addOverlay()
        startDaemon()
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate({ tickFps() }, 0, 50, TimeUnit.MILLISECONDS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        try { executor?.shutdownNow() } catch (t: Throwable) {}
        try {
            fpsView?.let { windowManager.removeView(it) }
        } catch (t: Throwable) {}
        fpsView = null
        super.onDestroy()
    }

    // 前台通知（Android 8+ 必须）
    private fun startForegroundCompat() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val ch = NotificationChannel(
                    "fps_overlay", "帧率悬浮窗", NotificationManager.IMPORTANCE_MIN
                )
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(ch)
                val n = Notification.Builder(this, "fps_overlay")
                    .setContentTitle("帧率悬浮窗运行中")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .build()
                startForeground(9527, n)
            }
        } catch (t: Throwable) {}
    }

    // 添加悬浮窗
    private fun addOverlay() {
        try {
            val tv = TextView(this)
            tv.text = "-- ~"
            tv.setTextColor(android.graphics.Color.WHITE)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            // 黄色圆角背景（Scene 同款）
            val bgDrawable = android.graphics.drawable.GradientDrawable()
            bgDrawable.setColor(android.graphics.Color.parseColor("#99B8860B"))   // 暗黄 + 高透明
            bgDrawable.cornerRadius = 50f   // 药丸形状
            tv.background = bgDrawable
            tv.setPadding(20, 6, 20, 6)

            val type = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            lp.gravity = Gravity.TOP or Gravity.START
            lp.x = 20
            lp.y = 120
            var downX = 0f
            var downY = 0f
            var startX = 0
            var startY = 0
            tv.setOnTouchListener { _, ev ->
                when (ev.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        downX = ev.rawX
                        downY = ev.rawY
                        startX = lp.x
                        startY = lp.y
                        true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        lp.x = startX + (ev.rawX - downX).toInt()
                        lp.y = startY + (ev.rawY - downY).toInt()
                        try { windowManager.updateViewLayout(tv, lp) } catch (t: Throwable) {}
                        true
                    }
                    else -> true
                }
            }
            windowManager.addView(tv, lp)
            fpsView = tv
        } catch (t: Throwable) {
            stopSelf()
        }
    }

    // 启动 root daemon：持续把帧时间写到 /data/local/tmp/fps_live
    private fun startDaemon() {
        try {
            // 一次 su 启动常驻 daemon：多平台采集（天玑 fpsgo / 高通 measured_fps）
            val cmd = "while true; do " +
                "{ " +
                "cat /sys/kernel/fpsgo/fstb/fpsgo_status 2>/dev/null; " +
                "cat /sys/class/drm/sde-crtc-0/measured_fps 2>/dev/null; " +
                "cat /sys/class/graphics/fb0/measured_fps 2>/dev/null; " +
                "cat /sys/devices/platform/soc/ae00000.qcom,mdss_mdp/drm/card0/card0-sde-crtc-0/measured_fps 2>/dev/null; " +
                "} > /data/local/tmp/fps_live.tmp; " +
                "mv /data/local/tmp/fps_live.tmp /data/local/tmp/fps_live; chmod 644 /data/local/tmp/fps_live; sleep 0.05; done"
            Runtime.getRuntime().exec(arrayOf(
                suBin(), "--mount-master", "-c", cmd
            ))
        } catch (t: Throwable) {}
    }

    // 读一次 FPS
    private fun tickFps() {
        try {
            val fps = readFps()
            if (fps != null && fps > 0) {
                fpsView?.post {
                    try {
                        fpsView?.text = String.format("%.0f~", fps)
                    } catch (t: Throwable) {}
                }
            }
        } catch (t: Throwable) {}
    }

    // 读帧率：fpsgo → gfxinfo → dumpsys
    private fun readFps(): Double? {
        android.util.Log.e("FPSD", "readFps start")
        // 1) 先直接读 fpsgo（免 root、零开销）
        sysfsFps()?.let { if (it > 1.0) return it }
        // 2) 兜底：刷新前台包名后 gfxinfo
        try { refreshActivePkg() } catch (t: Throwable) {}
        gfxinfoFps()?.let { if (it > 1.0) return it }
        // 3) dumpsys（Android 12 以下有效）
        return try {
            val f = File("/data/local/tmp/fps_live")
            if (!f.exists() || f.length() == 0L) return null
            parseFps(f.readText())
        } catch (t: Throwable) {
            null
        }
    }

    // 刷新当前前台包名
    private fun refreshActivePkg() {
        try {
            val pr = Runtime.getRuntime().exec(
                arrayOf(suBin(), "--mount-master", "-c",
                    "dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity")
            )
            val out = pr.inputStream.bufferedReader().readText()
            pr.waitFor()
            val m = Regex("([a-zA-Z0-9_.]+)/").find(out)
            if (m != null) activePkg = m.groupValues[1]
        } catch (t: Throwable) {}
    }

    // gfxinfo 帧统计（应用运行时有）
    private fun gfxinfoFps(): Double? {
        if (activePkg.isEmpty()) return null
        return try {
            val pr = Runtime.getRuntime().exec(
                arrayOf(suBin(), "--mount-master", "-c",
                    "dumpsys gfxinfo $activePkg 2>/dev/null")
            )
            val out = pr.inputStream.bufferedReader().readText()
            pr.waitFor()
            // 解析 "Total frames rendered: N" 与 "Janky frames"
            val total = Regex("Total frames rendered: (\\d+)").find(out)?.groupValues?.get(1)?.toIntOrNull()
            val janky = Regex("Janky frames: (\\d+)").find(out)?.groupValues?.get(1)?.toIntOrNull()
            if (total == null || janky == null || total <= 0) return null
            // 简化：用总帧数估算（需要时间窗口，此处仅作兜底）
            null
        } catch (t: Throwable) {
            null
        }
    }

    // 帧率通道（读 daemon 写的文件，不调 su）
    private fun sysfsFps(): Double? {
        // daemon 已经把 fpsgo 内容写到 fps_live，直接解析表格
        val live = File("/data/local/tmp/fps_live")
        val text = try { live.readText().trim() } catch (t: Throwable) { "" }
        if (text.isEmpty()) {
            android.util.Log.e("FPSD", "live empty")
            return null
        }
        // 兼容高通：如果内容是纯数字（measured_fps），直接返回
        val single = text.toDoubleOrNull()
        if (single != null && single > 1.0) {
            android.util.Log.e("FPSD", "measured_fps=" + single)
            return single
        }
        // 天玑 fpsgo 表格：优先匹配前台包名那一行（最准）；没有则取最大值
        val now = System.currentTimeMillis()
        if (now - lastPkgRefresh > 5000) {
            lastPkgRefresh = now
            try { refreshActivePkg() } catch (t: Throwable) {}
        }
        var best = 0.0
        var matched = 0.0
        for (l in text.lines()) {
            val cols = l.split(Regex("[\\t ]+")).filter { it.isNotBlank() }
            if (cols.size >= 4) {
                val v = cols[3].toDoubleOrNull() ?: continue
                if (v > best) best = v
                if (activePkg.isNotEmpty() && l.contains(activePkg) && v > matched) matched = v
            }
        }
        android.util.Log.e("FPSD", "fpsgo pkg=" + activePkg + " matched=" + matched + " best=" + best)
        return when {
            matched > 0 -> matched
            best > 0 -> best
            else -> null
        }
    }

    // 找当前层的名称（简化：从 dumpsys SurfaceFlinger 取第一个可见层）
    private fun findActiveLayer(): String? {
        return try {
            val pr = Runtime.getRuntime().exec(
                arrayOf(suBin(), "--mount-master", "-c",
                    "dumpsys SurfaceFlinger --list 2>/dev/null")
            )
            val out = pr.inputStream.bufferedReader().readLines()
            pr.waitFor()
            // 优先 SurfaceView / 全屏层
            out.firstOrNull { it.contains("SurfaceView") }
                ?: out.firstOrNull { it.isNotBlank() }
        } catch (t: Throwable) {
            null
        }
    }

    // 解析 dumpsys 输出（Scene 同款算法）：
    //  - 取最近 1 秒内的帧（present time）
    //  - 剔除异常帧（帧时间 > 100ms 的 jank）
    //  - FPS = 帧数 / 时间跨度，并做中位数平滑
    private fun parseFps(dump: String): Double? {
        val lines = dump.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return null
        val nowNs = System.nanoTime()
        val windowStart = nowNs - 1_000_000_000L   // 最近 1 秒
        val times = mutableListOf<Long>()
        for (i in 1 until lines.size) {
            val parts = lines[i].trim().split('\t', ' ')
            if (parts.size >= 2) {
                val t = parts[1].toLongOrNull() ?: continue
                if (t > 0) times.add(t)
            }
        }
        if (times.size < 2) return null
        // 只保留最近 1 秒的帧
        val recent = times.filter { it >= windowStart }.ifEmpty { times.takeLast(64) }
        if (recent.size < 2) return null
        // 剔除异常帧（帧时间 > 100ms 的卡顿帧，避免拉低平均）
        val spans = mutableListOf<Long>()
        for (i in 1 until recent.size) {
            val dt = recent[i] - recent[i - 1]
            if (dt in 1..100_000_000L) spans.add(dt)
        }
        if (spans.isEmpty()) return null
        spans.sort()
        // 中位数帧时间（抗毛刺）
        val median = spans[spans.size / 2]
        if (median <= 0) return null
        val fps = 1_000_000_000.0 / median.toDouble()
        return fps.coerceIn(1.0, 240.0)
    }
}
