with open("app/src/main/java/com/iamuzairmehmood/gamestats/manager/GamingModeManager.kt", "r") as f:
    content = f.read()

content = content.replace("GameBoostRepository", "GameStatsRepository")
content = content.replace("val repository: GameStatsRepository", "val repository: com.iamuzairmehmood.gamestats.data.GameStatsRepository")
content = content.replace("import com.iamuzairmehmood.gamestats.data.GameStatsRepository", "")

with open("app/src/main/java/com/iamuzairmehmood/gamestats/manager/GamingModeManager.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/AboutScreen.kt", "r") as f:
    content = f.read()

content = content.replace("DeviceCapabilities", "DeviceCapabilitiesReport")

with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/AboutScreen.kt", "w") as f:
    f.write(content)
