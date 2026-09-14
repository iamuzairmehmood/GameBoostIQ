import re
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# Fix broken imports (newlines as literals instead of actual newlines)
content = content.replace("import androidx.material.icons.filled.Delete\\nimport androidx.compose.material.icons.filled.Check", "import androidx.compose.material.icons.filled.Delete\nimport androidx.compose.material.icons.filled.Check")
content = content.replace("import androidx.palette.graphics.Palette\\nimport androidx.compose.runtime.Composable", "import androidx.palette.graphics.Palette\nimport androidx.compose.runtime.Composable")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
