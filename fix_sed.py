import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# I replaced ALL instances of displayColor with MaterialTheme.colorScheme.primary, which might break GameProfileDialog and the border of the card where it was declared. Let's fix that.
content = content.replace("val MaterialTheme.colorScheme.primary = if (hex != null) Color(android.graphics.Color.parseColor(hex)) else MaterialTheme.colorScheme.onSurfaceVariant", "val displayColor = if (hex != null) Color(android.graphics.Color.parseColor(hex)) else MaterialTheme.colorScheme.onSurfaceVariant")
content = content.replace("background(if (hex != null) MaterialTheme.colorScheme.primary else Color.Transparent)", "background(if (hex != null) displayColor else Color.Transparent)")
content = content.replace("color = MaterialTheme.colorScheme.primary)", "color = displayColor)")

# Fix the card border:
content = content.replace("1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)", "1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)")

# But wait, did I replace the declaration in GameItemCard?
content = content.replace("val MaterialTheme.colorScheme.primary = remember(game.profileColorHex, extractedColor)", "val cardDisplayColor = remember(game.profileColorHex, extractedColor)")
content = content.replace("1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)", "1.5.dp, cardDisplayColor.copy(alpha = 0.5f)")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
