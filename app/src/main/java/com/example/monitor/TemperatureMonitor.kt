package com.example.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import com.example.model.ThermalLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class TemperatureMonitor(private val context: Context) {

    data class ThermalData(
        val temperatureCelsius: Float,
        val level: ThermalLevel,
        val warningMessage: String?,
        val isHardwareThermalApiAvailable: Boolean
    )

    private val _thermalData = MutableStateFlow(
        ThermalData(
            temperatureCelsius = 36.5f,
            level = ThermalLevel.NORMAL,
            warningMessage = null,
            isHardwareThermalApiAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        )
    )
    val thermalData: StateFlow<ThermalData> = _thermalData.asStateFlow()

    private val isMonitoring = AtomicBoolean(false)
    private var powerManagerThermalListener: Any? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                val tempCelsius = if (rawTemp > 0) rawTemp / 10f else 36.0f
                updateThermalState(tempCelsius)
            }
        }
    }

    private fun updateThermalState(tempCelsius: Float) {
        val level = when {
            tempCelsius >= 45.0f -> ThermalLevel.CRITICAL
            tempCelsius >= 41.0f -> ThermalLevel.HOT
            tempCelsius >= 38.0f -> ThermalLevel.WARM
            else -> ThermalLevel.NORMAL
        }

        val warning = if (level == ThermalLevel.CRITICAL || level == ThermalLevel.HOT) {
            "Device temperature is high (${"%.1f".format(tempCelsius)}°C). Consider reducing gaming load."
        } else null

        _thermalData.value = _thermalData.value.copy(
            temperatureCelsius = tempCelsius,
            level = level,
            warningMessage = warning
        )
    }

    fun start() {
        if (isMonitoring.compareAndSet(false, true)) {
            // Register for battery temperature broadcasts
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val sticky = context.registerReceiver(batteryReceiver, filter)
            sticky?.let { intent ->
                val rawTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                if (rawTemp > 0) {
                    updateThermalState(rawTemp / 10f)
                }
            }

            // PowerManager thermal status listener on Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                    if (powerManager != null) {
                        val listener = PowerManager.OnThermalStatusChangedListener { status ->
                            val level = when (status) {
                                PowerManager.THERMAL_STATUS_NONE -> ThermalLevel.NORMAL
                                PowerManager.THERMAL_STATUS_LIGHT, PowerManager.THERMAL_STATUS_MODERATE -> ThermalLevel.WARM
                                PowerManager.THERMAL_STATUS_SEVERE -> ThermalLevel.HOT
                                PowerManager.THERMAL_STATUS_CRITICAL,
                                PowerManager.THERMAL_STATUS_EMERGENCY,
                                PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalLevel.CRITICAL
                                else -> ThermalLevel.NORMAL
                            }
                            val currentTemp = _thermalData.value.temperatureCelsius
                            val warning = if (level == ThermalLevel.CRITICAL || level == ThermalLevel.HOT) {
                                "Device temperature is high. Consider reducing gaming load."
                            } else null

                            _thermalData.value = _thermalData.value.copy(
                                level = level,
                                warningMessage = warning
                            )
                        }
                        powerManager.addThermalStatusListener(context.mainExecutor, listener)
                        powerManagerThermalListener = listener
                    }
                } catch (_: Exception) {
                    // Graceful fallback if OEM thermal service is restricted
                }
            }
        }
    }

    fun stop() {
        if (isMonitoring.compareAndSet(true, false)) {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManagerThermalListener != null) {
                try {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                    @Suppress("UNCHECKED_CAST")
                    (powerManagerThermalListener as? PowerManager.OnThermalStatusChangedListener)?.let {
                        powerManager?.removeThermalStatusListener(it)
                    }
                } catch (_: Exception) {}
                powerManagerThermalListener = null
            }
        }
    }
}
