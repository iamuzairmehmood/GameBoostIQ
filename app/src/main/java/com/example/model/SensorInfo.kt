package com.example.model

data class SensorInfo(
    val name: String,
    val typeString: String,
    val vendor: String,
    val powerMa: Float,
    val resolution: Float,
    val maximumRange: Float,
    val isAvailable: Boolean = true,
    val isWakeUpSensor: Boolean = false
)
