import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/SessionStatsScreen.kt", "r") as f:
    content = f.read()

if "CPU Usage" not in content:
    content = content.replace('SessionPerformanceChart("Network Ping (ms)", pingHist, Color(0xFF2196F3))', 
    'SessionPerformanceChart("Network Ping (ms)", pingHist, Color(0xFF2196F3))\n                    val cpuHist = parseStrToList(session.cpuHistoryStr, 0f, 100f, 40f)\n                    Spacer(modifier = Modifier.height(12.dp))\n                    SessionPerformanceChart("CPU Usage (%)", cpuHist, Color(0xFFE91E63))')
    with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/SessionStatsScreen.kt", "w") as f:
        f.write(content)
