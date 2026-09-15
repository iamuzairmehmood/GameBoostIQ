package com.iamuzairmehmood.GameStats.manager

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings

import com.iamuzairmehmood.GameStats.data.GamingSessionEntity
import com.iamuzairmehmood.GameStats.model.PerformanceMode
import com.iamuzairmehmood.GameStats.model.RestoreReport
import com.iamuzairmehmood.GameStats.model.ManualRestoreItem
import com.iamuzairmehmood.GameStats.monitor.PerformanceMonitor
import com.iamuzairmehmood.GameStats.service.GamingMonitoringService
import com.iamuzairmehmood.GameStats.service.OverlayService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GamingModeManager(
    private val context: Context,
    val performanceMonitor: PerformanceMonitor,
    private val repository: com.iamuzairmehmood.GameStats.data.GameStatsRepository
) {
    private val _isGamingModeActive = MutableStateFlow(false)
    val isGamingModeActive: StateFlow<Boolean> = _isGamingModeActive.asStateFlow()

    private val _activePerformanceMode = MutableStateFlow(PerformanceMode.PERFORMANCE)
    val activePerformanceMode: StateFlow<PerformanceMode> = _activePerformanceMode.asStateFlow()

    private val _latestRestoreReport = MutableStateFlow<RestoreReport?>(null)
    val latestRestoreReport: StateFlow<RestoreReport?> = _latestRestoreReport.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    private data class SavedSettingsState(
        val originalDndFilter: Int?,
        val originalRingerMode: Int?,
        val originalNotificationVolume: Int?,
        val originalMusicVolume: Int?,
        val appliedOptimizations: MutableList<String> = mutableListOf(),
        val manualItems: MutableList<ManualRestoreItem> = mutableListOf(),
        val sessionStartTime: Long = System.currentTimeMillis(),
        val startBatteryPercent: Int = 100,
        val gameName: String = "Global Gaming Mode",
        val performanceMode: PerformanceMode = PerformanceMode.PERFORMANCE
    )

    private var savedSessionState: SavedSettingsState? = null

    fun startGamingMode(mode: PerformanceMode = PerformanceMode.PERFORMANCE, gameName: String = "Gaming Mode", enableOverlay: Boolean = false) {
        if (_isGamingModeActive.value) return

        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val currentBattery = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100

        val originalDnd = if (notifManager?.isNotificationPolicyAccessGranted == true) notifManager.currentInterruptionFilter else null
        val originalRinger = audioManager?.ringerMode
        val originalNotifVol = audioManager?.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        val originalMusicVol = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)
        
        val appliedList = mutableListOf<String>()
        val manualList = mutableListOf<ManualRestoreItem>()

        if (notifManager != null && notifManager.isNotificationPolicyAccessGranted) {
            try {
                notifManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                appliedList.add("Do Not Disturb (Priority Mode) applied")
            } catch (_: Exception) {}
        } else {
            manualList.add(
                ManualRestoreItem(
                    settingName = "Do Not Disturb Notification Filter",
                    explanation = "Requires Do Not Disturb Access in Android Settings to automate.",
                    actionIntentName = Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                )
            )
        }

        if (audioManager != null) {
            try {
                if (mode == PerformanceMode.PERFORMANCE) {
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
                    appliedList.add("Notification audio stream muted to protect in-game soundscape")
                }
            } catch (_: Exception) {}
        }

        try {
            System.gc()
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            am?.killBackgroundProcesses(context.packageName)
            appliedList.add("App memory cache reclaimed & heap compacted via System.gc()")
        } catch (_: Exception) {}

        manualList.add(
            ManualRestoreItem(
                settingName = "Global Display Refresh Rate Switching",
                explanation = "Android security policy restricts universal display mode switching to System Settings without root.",
                actionIntentName = Settings.ACTION_DISPLAY_SETTINGS
            )
        )

        savedSessionState = SavedSettingsState(
            originalDndFilter = originalDnd,
            originalRingerMode = originalRinger,
            originalNotificationVolume = originalNotifVol,
            originalMusicVolume = originalMusicVol,
            appliedOptimizations = appliedList,
            manualItems = manualList,
            sessionStartTime = System.currentTimeMillis(),
            startBatteryPercent = currentBattery,
            gameName = gameName,
            performanceMode = mode
        )

        _activePerformanceMode.value = mode
        _isGamingModeActive.value = true

        performanceMonitor.start(isGamingMode = true, mode = mode, gameName = gameName)

        val serviceIntent = Intent(context, GamingMonitoringService::class.java).apply {
            action = GamingMonitoringService.ACTION_START
            putExtra(GamingMonitoringService.EXTRA_MODE, mode.name)
            putExtra(GamingMonitoringService.EXTRA_GAME, gameName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        if (enableOverlay && Settings.canDrawOverlays(context)) {
            val overlayIntent = Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_START
            }
            context.startService(overlayIntent)
            performanceMonitor.setOverlayActive(true)
        }
    }

    fun stopGamingMode() {
        if (!_isGamingModeActive.value) return

        val state = savedSessionState
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        val restoredItems = mutableListOf<String>()

        if (state != null) {
            if (state.originalDndFilter != null && notifManager != null && notifManager.isNotificationPolicyAccessGranted) {
                try {
                    notifManager.setInterruptionFilter(state.originalDndFilter)
                    restoredItems.add("Do Not Disturb filter reverted to original state")
                } catch (_: Exception) {}
            }

            if (state.originalNotificationVolume != null && audioManager != null) {
                try {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_NOTIFICATION,
                        state.originalNotificationVolume,
                        0
                    )
                    restoredItems.add("Notification audio volume restored to ${state.originalNotificationVolume}")
                } catch (_: Exception) {}
            }

            val serviceIntent = Intent(context, GamingMonitoringService::class.java).apply {
                action = GamingMonitoringService.ACTION_STOP
            }
            context.startService(serviceIntent)
            restoredItems.add("Gaming monitoring background service terminated")

            val overlayIntent = Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_STOP
            }
            context.stopService(overlayIntent)
            performanceMonitor.setOverlayActive(false)
            restoredItems.add("Floating HUD overlay detached from window manager")

            val sessionEnd = System.currentTimeMillis()
            val durationSec = (sessionEnd - state.sessionStartTime) / 1000L
            
            // Gather stats before stopping monitor
            val avgFps = if (performanceMonitor.sessionFpsReadings > 0) performanceMonitor.sessionTotalFps / performanceMonitor.sessionFpsReadings else 0
            val minFps = if (performanceMonitor.sessionMinFps == Int.MAX_VALUE) 0 else performanceMonitor.sessionMinFps
            val maxFps = performanceMonitor.sessionMaxFps
            
            val avgTemp = if (performanceMonitor.sessionTempReadings > 0) performanceMonitor.sessionTotalTemp / performanceMonitor.sessionTempReadings else 0.0
            val maxTemp = performanceMonitor.sessionMaxTemp
            
            val avgPing = if (performanceMonitor.sessionPingReadings > 0) performanceMonitor.sessionTotalPing / performanceMonitor.sessionPingReadings else 0
            
            val fpsGreen = performanceMonitor.fpsGreenSec
            val fpsOrange = performanceMonitor.fpsOrangeSec
            val fpsRed = performanceMonitor.fpsRedSec

            performanceMonitor.stop()

            val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val endBattery = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) ?: 100
            val batteryUsed = (state.startBatteryPercent - endBattery).coerceAtLeast(0)

            val sessionEntity = GamingSessionEntity(
                gamePackageName = "com.iamuzairmehmood.GameStats",
                gameName = state.gameName,
                startTime = state.sessionStartTime,
                endTime = sessionEnd,
                durationSeconds = durationSec,
                avgFps = avgFps.toFloat(),
                minFps = minFps.toFloat(),
                maxFps = maxFps.toFloat(),
                avgTemp = avgTemp.toFloat(),
                maxTemp = maxTemp.toFloat(),
                avgPingMs = avgPing.toInt(),
                batteryUsedPercent = batteryUsed,
                startBattery = state.startBatteryPercent,
                endBattery = endBattery,
                performanceMode = state.performanceMode.name,
                avgJitterMs = 0,
                packetLossPercent = 0.0f,
                wifiDataUsedBytes = 0,
                mobileDataUsedBytes = 0,
                totalDataUsedBytes = 0,
                fpsGreenDurationSec = fpsGreen,
                fpsOrangeDurationSec = fpsOrange,
                fpsRedDurationSec = fpsRed,
                cpuGreenDurationSec = 0L,
                cpuOrangeDurationSec = 0L,
                cpuRedDurationSec = 0L,
                fpsHistoryStr = performanceMonitor.fpsHistory.joinToString(","),
                tempHistoryStr = performanceMonitor.tempHistory.joinToString(","),
                pingHistoryStr = performanceMonitor.pingHistory.joinToString(","),
                cpuHistoryStr = performanceMonitor.cpuHistory.joinToString(",")
            )

            scope.launch {
                repository.recordSession(sessionEntity)
            }

            val report = RestoreReport(
                sessionDurationSeconds = durationSec,
                restoredSettingsCount = restoredItems.size,
                restoredSettings = restoredItems,
                manualRestorationCount = state.manualItems.size,
                manualRestorations = state.manualItems
            )
            _latestRestoreReport.value = report
        }

        savedSessionState = null
        _isGamingModeActive.value = false
    }

    fun dismissRestoreReport() {
        _latestRestoreReport.value = null
    }
}
