with open("app/src/main/java/com/iamuzairmehmood/gamestats/manager/GamingModeManager.kt", "r") as f:
    content = f.read()

content = content.replace("import com.iamuzairmehmood.gamestats.model.RestoreReport.ManualRestoreItem", "import com.iamuzairmehmood.gamestats.model.ManualRestoreItem")

with open("app/src/main/java/com/iamuzairmehmood/gamestats/manager/GamingModeManager.kt", "w") as f:
    f.write(content)
