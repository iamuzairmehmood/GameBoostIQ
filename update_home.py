import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'r') as f:
    content = f.read()

# Add onOpenDrawer parameter
content = content.replace(
    "onNavigate: (String) -> Unit\n)",
    "onNavigate: (String) -> Unit,\n    onOpenDrawer: () -> Unit\n)"
)

# Add hamburger icon
header = """        // App Header Brand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(28.dp).clickable { onOpenDrawer() }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {"""
            
content = re.sub(r'        // App Header Brand\s+Row\(\s+modifier = Modifier\.fillMaxWidth\(\),\s+horizontalArrangement = Arrangement\.Center,\s+verticalAlignment = Alignment\.CenterVertically\s+\) \{', header, content)
content = content.replace("import androidx.compose.material.icons.filled.Menu", "") # prevent duplicate if any
content = content.replace("import androidx.compose.material.icons.filled.Warning", "import androidx.compose.material.icons.filled.Warning\nimport androidx.compose.material.icons.filled.Menu")

# Remove NavCard section
nav_section_pattern = r'        // Navigation Action Modules.*?nav_about.*?\}\n'
content = re.sub(nav_section_pattern, '', content, flags=re.DOTALL)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'w') as f:
    f.write(content)
