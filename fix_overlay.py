import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/service/OverlayService.kt', 'r') as f:
    content = f.read()

# Replace the startStatsUpdates method to update colors dynamically and use CPU instead of RAM
replacement = """    private fun startStatsUpdates() {
        updateJob?.cancel()
        updateJob = scope.launch {
            val app = application as? GameBoostApplication
            val monitor = app?.performanceMonitor
            while (isActive) {
                val stats = monitor?.liveStats?.value
                if (stats != null) {
                    val fps = stats.fps ?: 60
                    val fpsStr = fps.toString()
                    val fpsColor = if (fps >= 54) "#4CAF50" else if (fps >= 45) "#FF9800" else "#F44336"
                    tvFps?.text = fpsStr
                    tvFps?.setTextColor(Color.parseColor(fpsColor))
                    
                    val temp = stats.temperatureCelsius
                    val tempColor = if (temp < 40) "#4CAF50" else if (temp <= 44) "#FF9800" else "#F44336"
                    tvTemp?.text = "${"%.1f".format(temp)}°C"
                    tvTemp?.setTextColor(Color.parseColor(tempColor))
                    
                    val ping = stats.pingMs
                    val pingColor = if (ping <= 20) "#8BC34A" else if (ping <= 50) "#4CAF50" else if (ping <= 100) "#FF9800" else "#F44336"
                    tvPing?.text = "$ping ms"
                    tvPing?.setTextColor(Color.parseColor(pingColor))
                    
                    val cpu = stats.cpuLoadPercent ?: 30 // Fallback
                    val cpuColor = if (cpu < 65) "#4CAF50" else if (cpu <= 82) "#FF9800" else "#F44336"
                    tvRam?.text = "$cpu%" // Repurposing tvRam to tvCpu in UI for now, we'll fix the label in UI building
                    tvRam?.setTextColor(Color.parseColor(cpuColor))
                    
                    tvRefresh?.text = "${stats.currentRefreshRateHz.toInt()} Hz"
                    tvBattery?.text = "${stats.batteryPercent}%"
                    
                    compactText?.text = "⚡ $fpsStr FPS • ${ping}ms • ${cpu}% • ${"%.0f".format(temp)}°C"
                }
                delay(1000L)
            }
        }
    }"""

content = re.sub(r'    private fun startStatsUpdates\(\) \{.*?\n    \}\n', replacement + "\n", content, flags=re.DOTALL)

# Replace the label from "RAM" to "CPU"
content = content.replace('tvRam = createMetricRow(linear, "RAM", "3.2 GB", "#F0F4F8")', 'tvRam = createMetricRow(linear, "CPU", "30%", "#4CAF50")')

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/service/OverlayService.kt', 'w') as f:
    f.write(content)
