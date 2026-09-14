import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# I will simply fallback the text blocks that fail since the displayColor variable wasn't created globally enough or the python regex missed it in previous step.

content = content.replace("displayColor.copy(alpha = 0.15f)", "MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)")
content = content.replace("color = when (game.performanceMode) {\\n                                \\"PERFORMANCE\\" -> ZoneRed\\n                                \\"BALANCED\\" -> displayColor", "color = when (game.performanceMode) {\\n                                \\"PERFORMANCE\\" -> ZoneRed\\n                                \\"BALANCED\\" -> MaterialTheme.colorScheme.primary")
content = content.replace("containerColor = displayColor", "containerColor = MaterialTheme.colorScheme.primary")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
