with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "onBack: () -> Unit) {" in line:
        lines.insert(i + 1, "    val context = LocalContext.current\n")
        break

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt', 'w') as f:
    f.writelines(lines)
