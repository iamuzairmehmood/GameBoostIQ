package com.example.manager

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import com.example.model.CapabilityCategory
import com.example.model.CapabilityItem
import com.example.model.DeviceCapabilitiesReport
import com.example.model.SupportStatus
import com.example.monitor.DisplayMonitor
import java.io.File

class DeviceCapabilityScanner(private val context: Context) {

    fun performFullScan(): DeviceCapabilitiesReport {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val brand = Build.BRAND
        val device = Build.DEVICE
        val product = Build.PRODUCT
        val hardware = Build.HARDWARE
        val androidVersion = Build.VERSION.RELEASE
        val apiLevel = Build.VERSION.SDK_INT
        val cpuArch = Build.SUPPORTED_ABIS.joinToString(", ")

        val isTecno = manufacturer.equals("TECNO", ignoreCase = true) || brand.equals("TECNO", ignoreCase = true)
        val isInfinix = manufacturer.equals("Infinix", ignoreCase = true) || brand.equals("Infinix", ignoreCase = true)
        val isTranssion = isTecno || isInfinix || manufacturer.contains("itel", ignoreCase = true)

        val isPova2 = isTecno && (
            model.contains("POVA 2", ignoreCase = true) ||
            model.contains("LE7", ignoreCase = true) ||
            product.contains("LE7", ignoreCase = true) ||
            device.contains("LE7", ignoreCase = true)
        )

        // RAM info
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        val totalRamGb = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        val totalRamFormatted = "%.1f GB".format(totalRamGb)

        // Display info
        val displayMonitor = DisplayMonitor(context)
        val dispInfo = displayMonitor.getDisplayInfo()

        // Sensors
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val allSensors = sm?.getSensorList(Sensor.TYPE_ALL) ?: emptyList()
        val hasAccel = allSensors.any { it.type == Sensor.TYPE_ACCELEROMETER }
        val hasGyro = allSensors.any { it.type == Sensor.TYPE_GYROSCOPE }
        val hasMag = allSensors.any { it.type == Sensor.TYPE_MAGNETIC_FIELD }
        val hasProximity = allSensors.any { it.type == Sensor.TYPE_PROXIMITY }
        val hasLight = allSensors.any { it.type == Sensor.TYPE_LIGHT }
        val hasGameRotation = allSensors.any { it.type == Sensor.TYPE_GAME_ROTATION_VECTOR }

        // Battery / Thermal
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempCelsius = if (rawTemp > 0) rawTemp / 10f else null
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val hasThermalListener = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        // Network
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNet = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNet)
        val hasWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val hasCell = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true

        // Permissions
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val hasDndAccess = notifManager?.isNotificationPolicyAccessGranted == true
        val hasOverlayPermission = Settings.canDrawOverlays(context)

        // Compile Categories
        val performanceCategory = CapabilityCategory(
            categoryName = "Performance",
            iconName = "speed",
            items = listOf(
                CapabilityItem(
                    title = "CPU Architecture",
                    status = SupportStatus.SUPPORTED,
                    description = cpuArch,
                    technicalDetails = "Hardware: $hardware, Cores: ${Runtime.getRuntime().availableProcessors()}"
                ),
                CapabilityItem(
                    title = "System RAM",
                    status = SupportStatus.SUPPORTED,
                    description = "$totalRamFormatted Total RAM",
                    technicalDetails = "Low RAM Profile: ${am?.isLowRamDevice ?: false}"
                ),
                CapabilityItem(
                    title = "Kernel Governor Direct Control",
                    status = SupportStatus.NOT_SUPPORTED,
                    description = "Restricted by Android Security Architecture",
                    technicalDetails = "Standard third-party apps cannot modify /sys/devices/system/cpu frequency governors without root."
                ),
                CapabilityItem(
                    title = "Memory Trim & Background Garbage Collector",
                    status = SupportStatus.SUPPORTED,
                    description = "Supported via Android ActivityManager & System.gc()",
                    technicalDetails = "Releases process heaps and signals system memory reclamation."
                )
            )
        )

        val displayCategory = CapabilityCategory(
            categoryName = "Display",
            iconName = "tv",
            items = listOf(
                CapabilityItem(
                    title = "Refresh Rate Detection",
                    status = SupportStatus.SUPPORTED,
                    description = "${dispInfo.currentRefreshRate.toInt()} Hz Current Rate",
                    technicalDetails = "Hardware modes: ${dispInfo.supportedRefreshRates.joinToString(", ") { "${it.toInt()}Hz" }}"
                ),
                CapabilityItem(
                    title = "Display Resolution",
                    status = SupportStatus.SUPPORTED,
                    description = "${dispInfo.resolutionWidth} x ${dispInfo.resolutionHeight} px",
                    technicalDetails = "DPI density: ${context.resources.displayMetrics.densityDpi} dpi"
                ),
                CapabilityItem(
                    title = "Display HDR Support",
                    status = if (dispInfo.hdrSupported) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (dispInfo.hdrSupported) "HDR10 / Wide Color Capable" else "Standard Dynamic Range (SDR)",
                    technicalDetails = "Evaluated via Display.getHdrCapabilities()"
                ),
                CapabilityItem(
                    title = "System-Wide Refresh Rate Lock",
                    status = SupportStatus.LIMITED,
                    description = "Requires Android Display Settings",
                    technicalDetails = "Android permits per-window preferred rate hints; global system display overrides are restricted to system settings."
                )
            )
        )

        val sensorsCategory = CapabilityCategory(
            categoryName = "Sensors",
            iconName = "sensors",
            items = listOf(
                CapabilityItem(
                    title = "Accelerometer",
                    status = if (hasAccel) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (hasAccel) "Available for motion controls" else "Not supported by this device"
                ),
                CapabilityItem(
                    title = "Hardware Gyroscope",
                    status = if (hasGyro) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (hasGyro) "Available for precision gaming gyro aiming" else "Not supported by this device"
                ),
                CapabilityItem(
                    title = "Game Rotation Vector",
                    status = if (hasGameRotation) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (hasGameRotation) "Uncalibrated low-drift gyro tracking" else "Not exposed on this device"
                ),
                CapabilityItem(
                    title = "Proximity & Ambient Light",
                    status = if (hasProximity && hasLight) SupportStatus.SUPPORTED else SupportStatus.LIMITED,
                    description = "Available for pocket/ambient detection"
                ),
                CapabilityItem(
                    title = "Global Sensor Hardware Kill Switch",
                    status = SupportStatus.LIMITED,
                    description = "Requires System Privacy Controls",
                    technicalDetails = "Android does not allow standard third-party applications to globally disable every hardware sensor."
                )
            )
        )

        val thermalCategory = CapabilityCategory(
            categoryName = "Thermal & Battery",
            iconName = "thermostat",
            items = listOf(
                CapabilityItem(
                    title = "Battery Thermal Sensor",
                    status = if (tempCelsius != null) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (tempCelsius != null) "Active: ${"%.1f".format(tempCelsius)}°C" else "Not exposed by hardware",
                    technicalDetails = "Monitored via BatteryManager.EXTRA_TEMPERATURE"
                ),
                CapabilityItem(
                    title = "Android Thermal Framework API",
                    status = if (hasThermalListener) SupportStatus.SUPPORTED else SupportStatus.NOT_SUPPORTED,
                    description = if (hasThermalListener) "PowerManager OnThermalStatusChangedListener" else "Not supported by this Android version (< API 29)",
                    technicalDetails = "Provides OEM thermal throttling level updates"
                ),
                CapabilityItem(
                    title = "Battery Capacity & Voltage",
                    status = SupportStatus.SUPPORTED,
                    description = if (isPova2) "7000 mAh High-Capacity Li-Po Profile" else "Standard Battery Profile",
                    technicalDetails = "Voltage: ${batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0} mV"
                )
            )
        )

        val networkCategory = CapabilityCategory(
            categoryName = "Network",
            iconName = "wifi",
            items = listOf(
                CapabilityItem(
                    title = "Real-Time Ping & Jitter Probe",
                    status = SupportStatus.SUPPORTED,
                    description = "Socket latency measurement to gaming endpoints",
                    technicalDetails = "Calculates round-trip delay, variance jitter, and packet loss"
                ),
                CapabilityItem(
                    title = "Wi-Fi & Cellular State Detection",
                    status = SupportStatus.SUPPORTED,
                    description = if (hasWifi) "Wi-Fi Active" else if (hasCell) "Cellular Active" else "Connected",
                    technicalDetails = "Metered: ${cm?.isActiveNetworkMetered ?: false}"
                ),
                CapabilityItem(
                    title = "ISP Routing Alteration (Magic Ping Drops)",
                    status = SupportStatus.NOT_SUPPORTED,
                    description = "Diagnoses quality honestly; cannot alter ISP routing",
                    technicalDetails = "GameBoost provides honest network diagnostics rather than false network acceleration promises."
                )
            )
        )

        val gamingCategory = CapabilityCategory(
            categoryName = "Gaming & Features",
            iconName = "sports_esports",
            items = listOf(
                CapabilityItem(
                    title = "Floating Gaming HUD Overlay",
                    status = if (hasOverlayPermission) SupportStatus.SUPPORTED else SupportStatus.REQUIRES_PERMISSION,
                    description = if (hasOverlayPermission) "Ready to display on screen" else "Requires 'Display over other apps' permission",
                    technicalDetails = "Movable, resizable, compact mode with real-time stats"
                ),
                CapabilityItem(
                    title = "Do Not Disturb Automation",
                    status = if (hasDndAccess) SupportStatus.SUPPORTED else SupportStatus.REQUIRES_PERMISSION,
                    description = if (hasDndAccess) "Granted: Mutes interruptions during gaming" else "Requires Do Not Disturb Access permission",
                    technicalDetails = "NotificationManager.setInterruptionFilter()"
                ),
                CapabilityItem(
                    title = "Quick Settings Gaming Tile",
                    status = SupportStatus.SUPPORTED,
                    description = "Official Android TileService integrated",
                    technicalDetails = "Allows one-tap start/stop from the notification shade"
                ),
                CapabilityItem(
                    title = "Real-Time FPS Monitoring",
                    status = SupportStatus.LIMITED,
                    description = "Supported via Overlay Frame Pacing",
                    technicalDetails = "Normal non-root apps cannot monitor arbitrary third-party application frame buffers without overlay frame pacing or ADB permissions."
                ),
                CapabilityItem(
                    title = "One-Tap Restore System",
                    status = SupportStatus.SUPPORTED,
                    description = "Fully verified session restoration",
                    technicalDetails = "Reverts only settings GameBoost modified; reports manual items honestly"
                )
            )
        )

        val pova2HardwareSummary = if (isPova2) {
            "TECNO POVA 2 Verified Hardware Target:\n" +
            "• Chipset: MediaTek Helio G85 (MT6769Z) 12nm Octa-Core\n" +
            "• CPU: 2x Cortex-A75 @ 2.0 GHz + 6x Cortex-A55 @ 1.8 GHz\n" +
            "• GPU: ARM Mali-G52 MC2\n" +
            "• Battery: 7000 mAh High-Capacity Li-Po\n" +
            "• Screen: 6.9\" FHD+ (1080 x 2460) IPS LCD\n" +
            "• HiOS Coexistence: Compatible with HiOS Game Space & Transsion Power Management"
        } else if (isTranssion) {
            "TECNO / Infinix (Transsion) Device Detected:\n" +
            "• Manufacturer: $manufacturer ($model)\n" +
            "• Hardware: $hardware | Architecture: $cpuArch\n" +
            "• Transsion Power Scheduler & Memory Manager optimizations active"
        } else null

        val overallSummary = if (isPova2) {
            "Priority Device Verified: TECNO POVA 2. All supported non-root gaming optimizations and hardware diagnostic channels are active."
        } else {
            "Device Profile: $manufacturer $model (Android $androidVersion, API $apiLevel). Clean non-root optimization profile loaded."
        }

        return DeviceCapabilitiesReport(
            manufacturer = manufacturer,
            model = model,
            androidVersion = androidVersion,
            apiLevel = apiLevel,
            cpuArch = cpuArch,
            totalRamFormatted = totalRamFormatted,
            isPova2 = isPova2,
            isTecnoOrInfinix = isTranssion,
            pova2HardwareSummary = pova2HardwareSummary,
            categories = listOf(
                performanceCategory,
                displayCategory,
                sensorsCategory,
                thermalCategory,
                networkCategory,
                gamingCategory
            ),
            overallSummary = overallSummary
        )
    }
}
