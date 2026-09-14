import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'r') as f:
    lines = f.readlines()

out = []
in_nav_cards = False
for line in lines:
    if "GameBoostIQ MODULES" in line or "FIREBOOST MODULES" in line:
        in_nav_cards = True
    
    if in_nav_cards:
        if "if (latestRestoreReport != null)" in line:
            in_nav_cards = False
            out.append(line)
    else:
        out.append(line)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'w') as f:
    f.writelines(out)
