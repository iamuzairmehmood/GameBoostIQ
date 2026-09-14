package com.iamuzairmehmood.GameStats.model

enum class PerformanceMode(
    val title: String,
    val description: String,
    val badgeColorHex: Long = 0xFF00E5FF
) {
    PERFORMANCE(
        title = "Performance",
        description = "Maximum supported gaming optimization. Mutes interruptions, activates high-priority frame pacing, and prioritizes gaming network diagnostics.",
        badgeColorHex = 0xFFFF3D00
    ),
    BALANCED(
        title = "Balanced",
        description = "Optimized balance between gaming performance and battery life. Moderate monitoring intervals with essential notification filtering.",
        badgeColorHex = 0xFF00E5FF
    ),
    BATTERY_SAVER(
        title = "Battery Saver",
        description = "Extends play sessions on low charge. Advises standard refresh rate, relaxes background polling, and reduces diagnostic resource overhead.",
        badgeColorHex = 0xFF00E676
    )
}
