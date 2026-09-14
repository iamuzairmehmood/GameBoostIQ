with open("app/src/main/java/com/iamuzairmehmood/gamestats/model/LiveGamingStats.kt", "r") as f:
    content = f.read()

content = content.replace("val cpuLoadPercent: Int = 30", "val cpuLoadPercent: Int? = null")
with open("app/src/main/java/com/iamuzairmehmood/gamestats/model/LiveGamingStats.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/HomeScreen.kt", "r") as f:
    content = f.read()
    
content = content.replace('"${stats.cpuLoadPercent}%"', 'if (stats.cpuLoadPercent != null) "${stats.cpuLoadPercent}%" else "Unavail"')
with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/HomeScreen.kt", "w") as f:
    f.write(content)
