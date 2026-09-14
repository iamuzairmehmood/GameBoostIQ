with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

content = content.replace("import android.net.Uri\nimport androidx.compose.material.icons.filled.Analytics\nimport androidx.compose.material.icons.filled.VideogameAsset\nimport androidx.compose.material.icons.filled.Palette\nimport androidx.compose.material.icons.filled.Security\npackage com.iamuzairmehmood.gameboostiq.ui\n", "package com.iamuzairmehmood.gameboostiq.ui\n\nimport android.net.Uri\nimport androidx.compose.material.icons.filled.Analytics\nimport androidx.compose.material.icons.filled.VideogameAsset\nimport androidx.compose.material.icons.filled.Palette\nimport androidx.compose.material.icons.filled.Security\n")

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
    f.write(content)
