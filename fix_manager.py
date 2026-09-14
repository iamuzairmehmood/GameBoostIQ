import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/manager/GamingModeManager.kt', 'r') as f:
    content = f.read()

content = content.replace("gamePackageName = state.targetPackage,", "gamePackageName = \"com.iamuzairmehmood.gameboostiq\",")
content = content.replace("gameName = state.targetGameName,", "gameName = state.gameName,")

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/manager/GamingModeManager.kt', 'w') as f:
    f.write(content)
