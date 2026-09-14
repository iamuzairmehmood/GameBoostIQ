import os
import glob

def replace_in_file(filepath, old_str, new_str):
    try:
        with open(filepath, 'r') as f:
            content = f.read()
        if old_str in content:
            new_content = content.replace(old_str, new_str)
            with open(filepath, 'w') as f:
                f.write(new_content)
    except UnicodeDecodeError:
        pass # Skip binary files

for root, dirs, files in os.walk('.'):
    for file in files:
        if file.endswith('.kt') or file.endswith('.xml') or file.endswith('.kts') or file.endswith('.json') or file.endswith('.md'):
            filepath = os.path.join(root, file)
            replace_in_file(filepath, 'gameboostiq', 'gamestats')
            replace_in_file(filepath, 'Game BoostIQ', 'GameStats')
            replace_in_file(filepath, 'GameBoostIQ', 'GameStats')
            replace_in_file(filepath, 'GameBoost', 'GameStats')
            replace_in_file(filepath, 'Game Boost', 'GameStats')
