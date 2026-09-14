package com.iamuzairmehmood.GameStats.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.iamuzairmehmood.GameStats.GameStatsApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OverlayService : Service() {

    companion object {
        const val ACTION_START = "com.example.gamestats.OVERLAY_START"
        const val ACTION_STOP = "com.example.gamestats.OVERLAY_STOP"
    }

    private var windowManager: WindowManager? = null
    private var overlayView: FrameLayout? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var isCompactMode = false
    private var updateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var tvFps: TextView? = null
    private var tvTemp: TextView? = null
    private var tvPing: TextView? = null
    private var tvRam: TextView? = null
    private var tvRefresh: TextView? = null
    private var tvBattery: TextView? = null

    private var fullContainer: LinearLayout? = null
    private var compactText: TextView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            removeOverlay()
            stopSelf()
            return START_NOT_STICKY
        }

        if (Settings.canDrawOverlays(this)) {
            showOverlay()
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showOverlay() {
        if (overlayView != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 60
            y = 120
        }

        val root = FrameLayout(this)
        overlayView = root

        // Background styling
        val bgDrawable = GradientDrawable().apply {
            setColor(Color.argb(220, 10, 14, 23)) // Semi-transparent dark obsidian
            cornerRadius = 24f
            setStroke(2, Color.parseColor("#00E5FF")) // Cyber cyan border
        }
        root.background = bgDrawable
        root.setPadding(24, 18, 24, 18)

        // Full stats container
        val linear = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        fullContainer = linear

        // Title row with compact toggle button
        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = "⚡ GAMEBOOST HUD"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 16, 8)
        }
        val modeToggle = TextView(this).apply {
            text = "[ - ]"
            setTextColor(Color.parseColor("#FF9100"))
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(8, 0, 0, 8)
            setOnClickListener {
                toggleCompactMode()
            }
        }
        titleRow.addView(title)
        titleRow.addView(modeToggle)
        linear.addView(titleRow)

        // Metric rows
        tvFps = createMetricRow(linear, "FPS", "60", "#00E5FF")
        tvTemp = createMetricRow(linear, "TEMP", "36°C", "#FF9100")
        tvPing = createMetricRow(linear, "PING", "38 ms", "#10B981")
        tvRam = createMetricRow(linear, "CPU", "30%", "#4CAF50")
        tvRefresh = createMetricRow(linear, "REFRESH", "60 Hz", "#7C4DFF")
        tvBattery = createMetricRow(linear, "BATTERY", "75%", "#10B981")

        root.addView(linear)

        // Compact pill text
        compactText = TextView(this).apply {
            text = "⚡ 60 FPS • 38ms"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            visibility = View.GONE
            setOnClickListener {
                toggleCompactMode()
            }
        }
        root.addView(compactText)

        // Touch listener for dragging
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var hasMoved = false

        root.setOnTouchListener { _, event ->
            val lp = layoutParams ?: return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = lp.x
                    initialY = lp.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    hasMoved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        hasMoved = true
                    }
                    lp.x = initialX + dx
                    lp.y = initialY + dy
                    windowManager?.updateViewLayout(root, lp)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!hasMoved) {
                        toggleCompactMode()
                    }
                    true
                }
                else -> false
            }
        }

        try {
            windowManager?.addView(root, layoutParams)
        } catch (_: Exception) {
            stopSelf()
            return
        }

        startStatsUpdates()
    }

    private fun toggleCompactMode() {
        isCompactMode = !isCompactMode
        if (isCompactMode) {
            fullContainer?.visibility = View.GONE
            compactText?.visibility = View.VISIBLE
        } else {
            fullContainer?.visibility = View.VISIBLE
            compactText?.visibility = View.GONE
        }
    }

    private fun createMetricRow(parent: LinearLayout, label: String, initialVal: String, colorHex: String): TextView {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 2, 0, 2)
        }
        val lbl = TextView(this).apply {
            text = label.padEnd(8)
            setTextColor(Color.parseColor("#94A3B8"))
            textSize = 11f
            typeface = Typeface.MONOSPACE
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f)
        }
        val value = TextView(this).apply {
            text = initialVal
            setTextColor(Color.parseColor(colorHex))
            textSize = 11f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        row.addView(lbl)
        row.addView(value)
        parent.addView(row)
        return value
    }

    private fun startStatsUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            val app = application as? GameStatsApplication
            val monitor = app?.performanceMonitor
            while (isActive) {
                val stats = monitor?.liveStats?.value
                if (stats != null) {
                    val fps = stats.fps ?: 60
                    val fpsStr = fps.toString()
                    val fpsColor = if (fps >= 54) "#4CAF50" else if (fps >= 45) "#FF9800" else "#F44336"
                    tvFps?.text = fpsStr
                    tvFps?.setTextColor(Color.parseColor(fpsColor))
                    
                    val temp = stats.temperatureCelsius
                    val tempColor = if (temp < 40) "#4CAF50" else if (temp <= 44) "#FF9800" else "#F44336"
                    tvTemp?.text = "${"%.1f".format(temp)}°C"
                    tvTemp?.setTextColor(Color.parseColor(tempColor))
                    
                    val ping = stats.pingMs
                    val pingColor = if (ping <= 20) "#8BC34A" else if (ping <= 50) "#4CAF50" else if (ping <= 100) "#FF9800" else "#F44336"
                    tvPing?.text = "$ping ms"
                    tvPing?.setTextColor(Color.parseColor(pingColor))
                    
                    val cpu = stats.cpuLoadPercent ?: 30 // Fallback
                    val cpuColor = if (cpu < 65) "#4CAF50" else if (cpu <= 82) "#FF9800" else "#F44336"
                    tvRam?.text = "$cpu%" // Repurposing tvRam to tvCpu in UI for now, we'll fix the label in UI building
                    tvRam?.setTextColor(Color.parseColor(cpuColor))
                    
                    tvRefresh?.text = "${stats.currentRefreshRateHz.toInt()} Hz"
                    tvBattery?.text = "${stats.batteryPercent}%"
                    
                    compactText?.text = "⚡ $fpsStr FPS • ${ping}ms • ${cpu}% • ${"%.0f".format(temp)}°C"
                }
                delay(1000L)
            }
        }
    }

    private fun removeOverlay() {
        updateJob?.cancel()
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (_: Exception) {}
            overlayView = null
        }
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }
}
