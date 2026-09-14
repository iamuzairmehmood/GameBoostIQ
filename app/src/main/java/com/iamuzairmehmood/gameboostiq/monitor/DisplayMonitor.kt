package com.iamuzairmehmood.gameboostiq.monitor

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Build
import android.provider.Settings
import android.view.Display
import android.view.WindowManager

class DisplayMonitor(private val context: Context) {

    data class DisplayInfo(
        val currentRefreshRate: Float,
        val supportedRefreshRates: List<Float>,
        val resolutionWidth: Int,
        val resolutionHeight: Int,
        val hdrSupported: Boolean,
        val isDynamicRefreshCapable: Boolean,
        val canModifySystemRefreshRateDirectly: Boolean,
        val explanation: String
    )

    fun getDisplayInfo(): DisplayInfo {
        var currentHz = 60f
        val supportedRates = mutableSetOf<Float>()
        var resWidth = 1080
        var resHeight = 2400
        var isHdr = false

        try {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            val defaultDisplay = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
                ?: (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay

            defaultDisplay?.let { disp ->
                val mode = disp.mode
                currentHz = mode.refreshRate
                resWidth = mode.physicalWidth
                resHeight = mode.physicalHeight

                for (m in disp.supportedModes) {
                    supportedRates.add(m.refreshRate)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val hdrCaps = disp.hdrCapabilities
                    if (hdrCaps != null && hdrCaps.supportedHdrTypes.isNotEmpty()) {
                        isHdr = true
                    }
                }
            }
        } catch (_: Exception) {
            supportedRates.add(60f)
        }

        if (supportedRates.isEmpty()) {
            supportedRates.add(currentHz)
        }

        val sortedRates = supportedRates.sorted()
        val isDynamic = sortedRates.size > 1

        val explanation = if (isDynamic) {
            "Display supports high refresh rates (${sortedRates.joinToString(" / ") { "${it.toInt()}Hz" }}). Android OS & OEM security policies permit per-window preferred rate hints; global system-wide display overrides require system display settings."
        } else {
            "Standard 60Hz display detected. Device hardware does not expose alternate display modes."
        }

        return DisplayInfo(
            currentRefreshRate = currentHz,
            supportedRefreshRates = sortedRates,
            resolutionWidth = resWidth,
            resolutionHeight = resHeight,
            hdrSupported = isHdr,
            isDynamicRefreshCapable = isDynamic,
            canModifySystemRefreshRateDirectly = false, // Standard non-root Android restriction
            explanation = explanation
        )
    }

    fun openDisplaySettingsIntent(): Intent {
        return Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}
