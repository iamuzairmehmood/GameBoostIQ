import re

# Fix AboutScreen.kt
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("import", "\nimport")
content = content.replace("package com.iamuzairmehmood.gameboostiq.ui\n\nimport", "package com.iamuzairmehmood.gameboostiq.ui\nimport")
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'w') as f:
    f.write(content)

# Fix GameBoostApp.kt
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

content = content.replace("import", "\nimport")
content = content.replace("package com.iamuzairmehmood.gameboostiq.ui\n\nimport", "package com.iamuzairmehmood.gameboostiq.ui\nimport")
# Fix the "unresolved reference AboutScreen" and others because imports were broken.
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
    f.write(content)

# Fix SessionStatsScreen.kt Unresolved reference 'VideogameAsset'
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/SessionStatsScreen.kt', 'r') as f:
    content = f.read()
if "import androidx.compose.material.icons.filled.VideogameAsset" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Delete", "import androidx.compose.material.icons.filled.Delete\nimport androidx.compose.material.icons.filled.VideogameAsset")
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/SessionStatsScreen.kt', 'w') as f:
    f.write(content)

# Fix HomeScreen.kt cpuLoadPercent (it should be cpuLoadPercent or cpuLoad? Let's check LiveGamingStats)
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/model/LiveGamingStats.kt', 'r') as f:
    stats_content = f.read()
# Let's see what the property is named... we will do it after.
