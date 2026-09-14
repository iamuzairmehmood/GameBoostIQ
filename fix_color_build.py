import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# Fix conflicting Color imports
content = content.replace("import androidx.compose.ui.graphics.Color\\nimport androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color")

# Fix Unresolved reference 'displayColor' inside GameItemCard
# Looking at the code, it's possible displayColor is out of scope for the Box and Text because they might be outside the Row or something.
# But it should be in GameItemCard. Let's make sure it is in the right block.

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)

