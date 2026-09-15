with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if "import androidx.compose.ui.graphics" in line:
        pass # drop it
    else:
        new_lines.append(line)

# Add correct imports near the top
import_block = """import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
"""
new_lines.insert(25, import_block)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.writelines(new_lines)
