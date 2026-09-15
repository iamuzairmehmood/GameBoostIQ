package com.iamuzairmehmood.GameStats.utils

object PerformanceColorUtils {
    const val LIGHT_GREEN = "#8BC34A"
    const val DARK_GREEN = "#4CAF50"
    const val ORANGE = "#FF9800"
    const val RED = "#F44336"
    
    fun getFpsColor(fps: Int, targetFps: Int): String {
        val ratio = fps.toFloat() / targetFps.toFloat()
        return when {
            ratio >= 0.90f -> DARK_GREEN
            ratio >= 0.67f -> ORANGE
            else -> RED
        }
    }
    
    fun getCpuColor(cpuPercent: Int): String {
        return when {
            cpuPercent <= 64 -> DARK_GREEN
            cpuPercent <= 82 -> ORANGE
            else -> RED
        }
    }
    
    fun getGpuColor(gpuPercent: Int): String {
        return when {
            gpuPercent <= 64 -> DARK_GREEN
            gpuPercent <= 82 -> ORANGE
            else -> RED
        }
    }
    
    fun getRamColor(ramPercent: Int): String {
        return when {
            ramPercent <= 69 -> DARK_GREEN
            ramPercent <= 84 -> ORANGE
            else -> RED
        }
    }
    
    fun getTempColor(tempC: Float): String {
        return when {
            tempC < 40f -> DARK_GREEN
            tempC <= 44f -> ORANGE
            else -> RED
        }
    }
    
    fun getPingColor(pingMs: Int): String {
        return when {
            pingMs <= 20 -> LIGHT_GREEN
            pingMs <= 50 -> DARK_GREEN
            pingMs <= 100 -> ORANGE
            else -> RED
        }
    }
    
    fun getBatteryColor(batteryPercent: Int): String {
        return when {
            batteryPercent >= 50 -> DARK_GREEN
            batteryPercent >= 20 -> ORANGE
            else -> RED
        }
    }
}
