package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.GameBoostApplication
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GamingMonitoringService : Service() {

    companion object {
        const val CHANNEL_ID = "gameboost_gaming_mode_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.gameboost.ACTION_START"
        const val ACTION_STOP = "com.example.gameboost.ACTION_STOP"
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_GAME = "extra_game"
    }

    private var updateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            val app = application as? GameBoostApplication
            app?.gamingModeManager?.stopGamingMode()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val modeName = intent?.getStringExtra(EXTRA_MODE) ?: "Performance"
        val gameName = intent?.getStringExtra(EXTRA_GAME) ?: "Gaming Mode"

        startForegroundWithNotification(gameName, modeName)
        startPeriodicNotificationUpdates(gameName, modeName)

        return START_STICKY
    }

    private fun startForegroundWithNotification(gameName: String, modeName: String) {
        val notification = buildNotification(gameName, modeName, "Initializing monitors...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            }
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildNotification(gameName: String, modeName: String, statsText: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, GamingMonitoringService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("GameBoost: $gameName ($modeName)")
            .setContentText(statsText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(openPendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop Gaming Mode", stopPendingIntent)
            .build()
    }

    private fun startPeriodicNotificationUpdates(gameName: String, modeName: String) {
        updateJob?.cancel()
        updateJob = scope.launch {
            val app = application as? GameBoostApplication
            val monitor = app?.performanceMonitor
            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            while (isActive) {
                delay(3000L)
                val stats = monitor?.liveStats?.value
                if (stats != null && notifManager != null) {
                    val fpsText = if (stats.fps != null) "${stats.fps} FPS" else "Active"
                    val statsLine = "$fpsText • ${"%.1f".format(stats.temperatureCelsius)}°C • ${stats.pingMs}ms • RAM ${stats.ramPercent}%"
                    notifManager.notify(NOTIFICATION_ID, buildNotification(gameName, modeName, statsLine))
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GameBoost Gaming Mode",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Displays live gaming statistics and quick stop controls"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        updateJob?.cancel()
        super.onDestroy()
    }
}
