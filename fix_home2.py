import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'r') as f:
    content = f.read()

bad_block = """        Spacer(modifier = Modifier.height(20.dp))
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }"""

good_block = """        Spacer(modifier = Modifier.height(24.dp))
    }"""

content = content.replace(bad_block, good_block)
with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/HomeScreen.kt', 'w') as f:
    f.write(content)
