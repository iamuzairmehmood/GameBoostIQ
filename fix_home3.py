import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'r') as f:
    content = f.read()

# Replace the malformed block
content = re.sub(
    r"        Spacer\(modifier = Modifier\.height\(20\.dp\)\)\n\s+\)\n\s+\}\n\s+Spacer\(modifier = Modifier\.height\(24\.dp\)\)",
    r"        Spacer(modifier = Modifier.height(24.dp))",
    content
)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'w') as f:
    f.write(content)
