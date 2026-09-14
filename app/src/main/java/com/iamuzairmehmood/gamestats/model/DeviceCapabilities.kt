package com.iamuzairmehmood.gamestats.model

enum class SupportStatus(val label: String, val colorHex: Long) {
    SUPPORTED("SUPPORTED", 0xFF10B981),
    LIMITED("LIMITED", 0xFFF59E0B),
    NOT_SUPPORTED("NOT SUPPORTED", 0xFFEF4444),
    REQUIRES_PERMISSION("REQUIRES PERMISSION", 0xFF8B5CF6)
}

data class CapabilityItem(
    val title: String,
    val status: SupportStatus,
    val description: String,
    val technicalDetails: String? = null
)

data class CapabilityCategory(
    val categoryName: String,
    val iconName: String,
    val items: List<CapabilityItem>
)

data class DeviceCapabilitiesReport(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val cpuArch: String,
    val totalRamFormatted: String,
    val isPova2: Boolean,
    val isTecnoOrInfinix: Boolean,
    val pova2HardwareSummary: String?,
    val categories: List<CapabilityCategory>,
    val overallSummary: String
)
