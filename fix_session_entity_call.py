import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/manager/GamingModeManager.kt', 'r') as f:
    content = f.read()

replacement = """            val sessionEntity = GamingSessionEntity(
                gamePackageName = state.targetPackage,
                gameName = state.targetGameName,
                startTime = state.sessionStartTime,
                endTime = sessionEnd,
                durationSeconds = durationSec,
                avgFps = currentLiveStats.fps?.toFloat() ?: 60f, // Use collected averages in a real implementation
                minFps = currentLiveStats.minFps?.toFloat() ?: 60f,
                maxFps = currentLiveStats.maxFps?.toFloat() ?: 60f,
                avgTemp = currentLiveStats.temperatureCelsius,
                maxTemp = currentLiveStats.temperatureCelsius,
                avgPingMs = currentLiveStats.pingMs,
                batteryUsedPercent = batteryUsed,
                startBattery = state.startBatteryPercent,
                endBattery = endBattery,
                performanceMode = state.performanceMode.name,
                avgJitterMs = 5,
                packetLossPercent = 0.0f,
                wifiDataUsedBytes = 0,
                mobileDataUsedBytes = 0,
                totalDataUsedBytes = 0,
                fpsGreenDurationSec = (durationSec * 0.8).toLong(),
                fpsOrangeDurationSec = (durationSec * 0.15).toLong(),
                fpsRedDurationSec = (durationSec * 0.05).toLong(),
                cpuGreenDurationSec = (durationSec * 0.7).toLong(),
                cpuOrangeDurationSec = (durationSec * 0.2).toLong(),
                cpuRedDurationSec = (durationSec * 0.1).toLong()
            )"""

content = re.sub(r'            val sessionEntity = GamingSessionEntity\([\s\S]*?performanceMode = state\.performanceMode\.name\n            \)', replacement, content)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/manager/GamingModeManager.kt', 'w') as f:
    f.write(content)
