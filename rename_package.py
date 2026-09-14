import os
import glob

old_pkg = "com.iamuzairmehmood.gamestats"
new_pkg = "com.iamuzairmehmood.GameStats"
old_dir = "app/src/main/java/com/iamuzairmehmood/gamestats"
new_dir = "app/src/main/java/com/iamuzairmehmood/GameStats"

if not os.path.exists(new_dir):
    os.rename(old_dir, new_dir)

for root, dirs, files in os.walk("app"):
    for file in files:
        if file.endswith(".kt") or file.endswith(".xml") or file.endswith(".kts") or file.endswith(".pro"):
            filepath = os.path.join(root, file)
            with open(filepath, "r") as f:
                content = f.read()
            if old_pkg in content:
                content = content.replace(old_pkg, new_pkg)
                with open(filepath, "w") as f:
                    f.write(content)

