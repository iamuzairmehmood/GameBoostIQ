import re
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.foundation.lazy.itemsIndexedIndexed", "import androidx.compose.foundation.lazy.itemsIndexed")
content = content.replace("import androidx.compose.foundation.lazy.itemsIndexed\nimport androidx.compose.foundation.lazy.itemsIndexed", "import androidx.compose.foundation.lazy.itemsIndexed")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
