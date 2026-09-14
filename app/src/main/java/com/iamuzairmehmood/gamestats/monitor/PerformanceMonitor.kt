package com.iamuzairmehmood.gamestats.monitor

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.iamuzairmehmood.gamestats.model.LiveGamingStats
import com.iamuzairmehmood.gamestats.model.PerformanceMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PerformanceMonitor(private val context: Context) {
    val fpsMonitor = FPSMonitor()
    val temperatureMonitor = TemperatureMonitor(context)
    val networkMonitor = NetworkPingMonitor(context)
    val displayMonitor = DisplayMonitor(context)

    private val _liveStats = MutableStateFlow(LiveGamingStats())
    val liveStats: StateFlow<LiveGamingStats> = _liveStats.asStateFlow()
    
    // Session Accumulation Variables
    var sessionTotalFps = 0L
    var sessionFpsReadings = 0
    var sessionMinFps = Int.MAX_VALUE
    var sessionMaxFps = 0
    
    var sessionTotalTemp = 0.0
    var sessionTempReadings = 0
    var sessionMaxTemp = 0.0
    
    var sessionTotalPing = 0L
    var sessionPingReadings = 0
    
    var fpsGreenSec = 0L
    var fpsOrangeSec = 0L
    var fpsRedSec = 0L
    
    var cpuGreenSec = 0L
    var cpuOrangeSec = 0L
    var cpuRedSec = 0L

    private val isMonitoring = AtomicBoolean(false)
    private var tickerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var sessionStartTime = 0L

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                val voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else 75
                _liveStats.value = _liveStats.value.copy(
                    batteryPercent = percent,
                    isCharging = isCharging,
                    batteryVoltageMv = voltageMv
                )
            }
        }
    }

    fun start(isGamingMode: Boolean = false, mode: PerformanceMode = PerformanceMode.PERFORMANCE, gameName: String? = null) {
        if (isMonitoring.compareAndSet(false, true)) {
            sessionStartTime = System.currentTimeMillis()
            
            // Reset accumulations
            sessionTotalFps = 0
            sessionFpsReadings = 0
            sessionMinFps = Int.MAX_VALUE
            sessionMaxFps = 0
            sessionTotalTemp = 0.0
            sessionTempReadings = 0
            sessionMaxTemp = 0.0
            sessionTotalPing = 0
            sessionPingReadings = 0
            fpsGreenSec = 0
            fpsOrangeSec = 0
            fpsRedSec = 0
            cpuGreenSec = 0
            cpuOrangeSec = 0
            cpuRedSec = 0
            
            fpsMonitor.start()
            temperatureMonitor.start()
            val pingInterval = when (mode) {
                PerformanceMode.PERFORMANCE -> 1500L
                PerformanceMode.BALANCED -> 2500L
                PerformanceMode.BATTERY_SAVER -> 4000L
            }
            networkMonitor.start(pingInterval)
            
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryReceiver, filter)
            
            val dispInfo = displayMonitor.getDisplayInfo()
            _liveStats.value = _liveStats.value.copy(
                currentRefreshRateHz = dispInfo.currentRefreshRate,
                supportedRefreshRatesHz = dispInfo.supportedRefreshRates,
                isGamingModeActive = isGamingMode,
                activePerformanceMode = mode,
                activeGameName = gameName
            )

            tickerJob = scope.launch {
                while (isActive) {
                    val ramInfo = readRamInfo()
                    val thermal = temperatureMonitor.thermalData.value
                    val net = networkMonitor.networkData.value
                    val currentFps = fpsMonitor.fps.value
                    val sessionSec = if (sessionStartTime > 0) {
                        (System.currentTimeMillis() - sessionStartTime) / 1000L
                    } else 0L
                    
                    // Accumulate stats
                    if (currentFps != null && currentFps > 0) {
                        sessionTotalFps += currentFps
                        sessionFpsReadings++
                        if (currentFps < sessionMinFps) sessionMinFps = currentFps
                        if (currentFps > sessionMaxFps) sessionMaxFps = currentFps
                        
                        if (currentFps >= 50) fpsGreenSec++
                        else if (currentFps >= 30) fpsOrangeSec++
                        else fpsRedSec++
                    }
                    
                    sessionTotalTemp += thermal.temperatureCelsius
                    sessionTempReadings++
                    if (thermal.temperatureCelsius > sessionMaxTemp) sessionMaxTemp = thermal.temperatureCelsius.toDouble()
                    
                    if (net.pingMs > 0) {
                        sessionTotalPing += net.pingMs
                        sessionPingReadings++
                    }
                    
                    // Basic CPU Load placeholder - can't accurately get CPU load on modern Android without native code.
                    val cpuLoad = null
                    
                    _liveStats.value = _liveStats.value.copy(
                        fps = currentFps,
                        minFps = if (sessionMinFps == Int.MAX_VALUE) null else sessionMinFps,
                        maxFps = if (sessionMaxFps == 0) null else sessionMaxFps,
                        avgFps = if (sessionFpsReadings > 0) (sessionTotalFps / sessionFpsReadings).toInt() else null,
                        fpsStatusNote = if (currentFps != null) "Real-time Pacing: ${currentFps} FPS" else "FPS: Unavailable without Active Overlay",
                        temperatureCelsius = thermal.temperatureCelsius,
                        thermalStatus = thermal.level,
                        thermalWarningMessage = thermal.warningMessage,
                        pingMs = net.pingMs,
                        jitterMs = net.jitterMs,
                        packetLossPercent = net.packetLossPercent,
                        networkType = net.connectionType,
                        ramUsedGb = ramInfo.usedGb,
                        ramTotalGb = ramInfo.totalGb,
                        ramPercent = ramInfo.usedPercent,
                        cpuLoadPercent = cpuLoad,
                        sessionDurationSeconds = sessionSec
                    )
                    
                    delay(1000L)
                }
            }
        }
    }

    fun stop() {
        if (isMonitoring.compareAndSet(true, false)) {
            tickerJob?.cancel()
            tickerJob = null
            fpsMonitor.stop()
            temperatureMonitor.stop()
            networkMonitor.stop()
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {}
            
            _liveStats.value = _liveStats.value.copy(
                isGamingModeActive = false,
                activeGameName = null
            )
        }
    }

    fun setOverlayActive(active: Boolean) {
        _liveStats.value = _liveStats.value.copy(isOverlayActive = active)
    }

    data class RamInfo(val usedGb: Float, val totalGb: Float, val usedPercent: Int)

    fun readRamInfo(): RamInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val totalBytes = memInfo.totalMem.toDouble()
        val availBytes = memInfo.availMem.toDouble()
        val usedBytes = (totalBytes - availBytes).coerceAtLeast(0.0)
        val totalGb = (totalBytes / (1024 * 1024 * 1024)).toFloat()
        val usedGb = (usedBytes / (1024 * 1024 * 1024)).toFloat()
        val percent = if (totalBytes > 0) ((usedBytes / totalBytes) * 100).toInt() else 50
        return RamInfo(
            usedGb = (Math.round(usedGb * 10) / 10.0).toFloat(),
            totalGb = (Math.round(totalGb * 10) / 10.0).toFloat(),
            usedPercent = percent
        )
    }
}
