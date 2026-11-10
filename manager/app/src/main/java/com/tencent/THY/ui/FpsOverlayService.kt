package com.tencent.THY.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundCompat()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        addOverlay()
        executor = Executors.newSingleThreadScheduledExecutor()
        executor?.scheduleAtFixedRate({ tickFps() }, 0, 1, TimeUnit.SECONDS)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            tv.text = "-- FPS"
            tv.setTextColor(Color.WHITE)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            tv.setBackgroundColor(Color.parseColor("#AA000000"))
            tv.setPadding(16, 6, 16, 6)

            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
            lp.gravity = Gravity.TOP or Gravity.START
            lp.x = 20
            lp.y = 120
            windowManager.addView(tv, lp)
            fpsView = tv
        } catch (t: Throwable) {
            stopSelf()
        }
    }

    // 读一次 FPS
    private fun tickFps() {
        try {
            val fps = readFps()
            if (fps != null && fps > 0) {
                fpsView?.post {
                    try {
                        fpsView?.text = String.format("%.0f FPS", fps)
                    } catch (t: Throwable) {}
                }
            }
        } catch (t: Throwable) {}
    }

    // root 执行 dumpsys SurfaceFlinger --latency <主层>，取最近帧时间算 FPS
    private fun readFps(): Double? {
        return try {
            // 找到当前焦点窗口的 layer
            val layer = findActiveLayer() ?: return null
            val pr = Runtime.getRuntime().exec(
                arrayOf("/data/adb/ksu/bin/su", "--mount-master", "-c",
                    "dumpsys SurfaceFlinger --latency \"$layer\" 2>/dev/null")
            )
            val out = pr.inputStream.bufferedReader().readText()
            pr.waitFor()
            parseFps(out)
        } catch (t: Throwable) {
            null
        }
    }

    // 找当前层的名称（简化：从 dumpsys SurfaceFlinger 取第一个可见层）
    private fun findActiveLayer(): String? {
        return try {
            val pr = Runtime.getRuntime().exec(
                arrayOf("/data/adb/ksu/bin/su", "--mount-master", "-c",
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

    // 解析 dumpsys 输出：第一行是刷新率，之后每行是 (desiredPresent, actualPresent, frameReady)
    private fun parseFps(dump: String): Double? {
        val lines = dump.lines().filter { it.isNotBlank() }
        if (lines.size < 2) return null
        val times = mutableListOf<Long>()
        for (i in 1 until lines.size) {
            val parts = lines[i].trim().split('\t', ' ')
            if (parts.size >= 2) {
                val t = parts[1].toLongOrNull() ?: continue
                if (t > 0) times.add(t)
            }
        }
        if (times.size < 2) return null
        val minT = times.minOrNull() ?: return null
        val maxT = times.maxOrNull() ?: return null
        val span = maxT - minT
        if (span <= 0) return null
        // times 是纳秒
        val fps = (times.size - 1).toDouble() / (span.toDouble() / 1_000_000_000.0)
        return fps.coerceIn(1.0, 240.0)
    }
}
