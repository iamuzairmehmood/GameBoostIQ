package com.iamuzairmehmood.GameStats.model

data class LiveGamingStats(
    val fps: Int? = null,
    val minFps: Int? = null,
    val maxFps: Int? = null,
    val avgFps: Int? = null,
    val fpsStatusNote: String = "Active (Overlay Frame Pacer)",
    val isFpsAvailable: Boolean = true,
    val cpuLoadPercent: Int? = null,
    
    val temperatureCelsius: Float = 36.5f,
    val thermalStatus: ThermalLevel = ThermalLevel.NORMAL,
    val thermalWarningMessage: String? = null,
    
    val pingMs: Int = 38,
    val jitterMs: Int = 4,
    val packetLossPercent: Int = 0,
    val networkType: String = "Wi-Fi",
    
    val ramUsedGb: Float = 3.2f,
    val ramTotalGb: Float = 6.0f,
    val ramPercent: Int = 53,
    
    val currentRefreshRateHz: Float = 60f,
    val supportedRefreshRatesHz: List<Float> = listOf(60f),
    
    val batteryPercent: Int = 75,
    val isCharging: Boolean = false,
    val batteryHealth: String = "Good",
    val batteryVoltageMv: Int = 4120,
    
    val isGamingModeActive: Boolean = false,
    val activePerformanceMode: PerformanceMode = PerformanceMode.PERFORMANCE,
    val activeGameName: String? = null,
    val sessionDurationSeconds: Long = 0L,
    val isOverlayActive: Boolean = false
)

enum class ThermalLevel(val label: String, val colorHex: Long) {
    NORMAL("Normal", 0xFF10B981),
    WARM("Warm", 0xFFF59E0B),
    HOT("Hot", 0xFFFF9100),
    CRITICAL("Critical", 0xFFEF4444)
}
