package com.example.monitor

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.provider.Settings
import com.example.model.SensorInfo

class SensorCapabilityManager(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    fun getDetectedSensors(): List<SensorInfo> {
        val result = mutableListOf<SensorInfo>()
        val allSensors = sensorManager?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()

        for (s in allSensors) {
            val typeStr = when (s.type) {
                Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
                Sensor.TYPE_GYROSCOPE -> "Gyroscope"
                Sensor.TYPE_MAGNETIC_FIELD -> "Magnetometer"
                Sensor.TYPE_PROXIMITY -> "Proximity Sensor"
                Sensor.TYPE_LIGHT -> "Ambient Light Sensor"
                Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
                Sensor.TYPE_GRAVITY -> "Gravity Sensor"
                Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
                Sensor.TYPE_STEP_COUNTER -> "Step Counter"
                Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
                Sensor.TYPE_PRESSURE -> "Barometer"
                Sensor.TYPE_GAME_ROTATION_VECTOR -> "Game Rotation Vector"
                else -> s.stringType.substringAfterLast(".").replace("_", " ").capitalizeWords()
            }

            result.add(
                SensorInfo(
                    name = s.name,
                    typeString = typeStr,
                    vendor = s.vendor,
                    powerMa = s.power,
                    resolution = s.resolution,
                    maximumRange = s.maximumRange,
                    isAvailable = true,
                    isWakeUpSensor = s.isWakeUpSensor
                )
            )
        }

        // If specific core gaming sensors were missing, add an entry showing unavailable
        val hasGyro = allSensors.any { it.type == Sensor.TYPE_GYROSCOPE }
        if (!hasGyro) {
            result.add(
                SensorInfo(
                    name = "Hardware Gyroscope",
                    typeString = "Gyroscope",
                    vendor = "Not Present",
                    powerMa = 0f,
                    resolution = 0f,
                    maximumRange = 0f,
                    isAvailable = false
                )
            )
        }

        return result.sortedBy { !it.isAvailable }
    }

    fun openSensorPrivacySettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_PRIVACY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return if (intent.resolveActivity(context.packageManager) != null) {
            intent
        } else {
            Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
