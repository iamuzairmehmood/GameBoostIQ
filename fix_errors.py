import re

# Fix HomeScreen.kt
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("lastSession.appName", "lastSession.gameName")
content = content.replace("lastSession.durationMinutes", "lastSession.durationSeconds / 60")
content = content.replace("lastSession.averageFps", "lastSession.avgFps.toInt()")
content = content.replace("lastSession.averagePing", "lastSession.avgPingMs")
content = content.replace("lastSession.maxTemperature", "lastSession.maxTemp.toInt()")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(content)

# Fix GameLibraryScreen.kt
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

imports = """import androidx.compose.runtime.LaunchedEffect
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.Image"""

if "import androidx.compose.runtime.LaunchedEffect" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", imports + "\nimport androidx.compose.runtime.Composable")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)

