import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GamingSessionEntity.kt", "r") as f:
    content = f.read()
if "fpsHistoryStr" not in content:
    content = content.replace("val cpuRedDurationSec: Long = 0", "val cpuRedDurationSec: Long = 0,\n    val fpsHistoryStr: String = \"\",\n    val tempHistoryStr: String = \"\",\n    val pingHistoryStr: String = \"\",\n    val cpuHistoryStr: String = \"\"")
    with open("app/src/main/java/com/iamuzairmehmood/GameStats/data/GamingSessionEntity.kt", "w") as f:
        f.write(content)


with open("app/src/main/java/com/iamuzairmehmood/GameStats/monitor/PerformanceMonitor.kt", "r") as f:
    content = f.read()
if "val fpsHistory = mutableListOf<Float>()" not in content:
    content = content.replace("var cpuRedSec = 0L", "var cpuRedSec = 0L\n\n    val fpsHistory = mutableListOf<Float>()\n    val tempHistory = mutableListOf<Float>()\n    val pingHistory = mutableListOf<Float>()\n    val cpuHistory = mutableListOf<Float>()")
    
    start_reset = """            cpuGreenSec = 0
            cpuOrangeSec = 0
            cpuRedSec = 0"""
    content = content.replace(start_reset, start_reset + "\n\n            fpsHistory.clear()\n            tempHistory.clear()\n            pingHistory.clear()\n            cpuHistory.clear()")
    
    ticker_job = "delay(1000L)"
    content = content.replace(ticker_job, """// Record history every 5 seconds to prevent huge data
                    if (sessionSec % 5 == 0L) {
                        fpsHistory.add(currentFps?.toFloat() ?: 0f)
                        tempHistory.add(thermal.temperatureCelsius)
                        pingHistory.add(net.pingMs.toFloat())
                        cpuHistory.add(cpuLoad?.toFloat() ?: 0f)
                    }
                    
                    delay(1000L)""")
    with open("app/src/main/java/com/iamuzairmehmood/GameStats/monitor/PerformanceMonitor.kt", "w") as f:
        f.write(content)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/manager/GamingModeManager.kt", "r") as f:
    content = f.read()

if "fpsHistoryStr =" not in content:
    content = content.replace("cpuRedDurationSec = 0L", """cpuRedDurationSec = 0L,
                fpsHistoryStr = performanceMonitor.fpsHistory.joinToString(","),
                tempHistoryStr = performanceMonitor.tempHistory.joinToString(","),
                pingHistoryStr = performanceMonitor.pingHistory.joinToString(","),
                cpuHistoryStr = performanceMonitor.cpuHistory.joinToString(",")""")
    with open("app/src/main/java/com/iamuzairmehmood/GameStats/manager/GamingModeManager.kt", "w") as f:
        f.write(content)

