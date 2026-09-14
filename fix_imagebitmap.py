import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.ui.graphics.asImageBitmap" not in content:
    content = content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.asImageBitmap")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
