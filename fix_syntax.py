import re
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    text = f.read()

# Just use regex to replace it
text = re.sub(r'import androidx\.compose\.ui\.graphics\.Brush\\nimport androidx\.compose\.ui\.graphics\.Color', 
              'import androidx.compose.ui.graphics.Brush\nimport androidx.compose.ui.graphics.Color', text)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(text)
