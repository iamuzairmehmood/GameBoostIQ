with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/SessionStatsScreen.kt", "r") as f:
    lines = f.readlines()

out_lines = []
for line in lines:
    if line.startswith("import") and "@Composable" not in line:
        pass # Skip the misplaced imports
    out_lines.append(line)

content = "".join(out_lines)

# Just kidding, let me just rewrite the whole file, it's easier.
