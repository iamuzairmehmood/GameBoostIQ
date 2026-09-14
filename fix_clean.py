import re

def clean_imports(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the package declaration
    pkg_match = re.search(r'^package\s+[a-zA-Z0-9_.]+', content, re.MULTILINE)
    if not pkg_match:
        return
    
    pkg_decl = pkg_match.group(0)
    
    # Strip all package and import lines, we will re-inject them from a parsed list
    imports = set(re.findall(r'import\s+[a-zA-Z0-9_.*]+', content))
    
    # Remove package and imports from the body
    body = content
    body = re.sub(r'^package\s+[a-zA-Z0-9_.]+\s*', '', body, flags=re.MULTILINE)
    body = re.sub(r'import\s+[a-zA-Z0-9_.*]+\s*', '', body)
    
    # Clean up multiple newlines
    body = re.sub(r'\n{3,}', '\n\n', body)
    
    # Reassemble
    new_content = f"{pkg_decl}\n\n"
    for imp in sorted(imports):
        new_content += f"{imp}\n"
    
    new_content += "\n" + body.strip() + "\n"
    
    with open(filepath, 'w') as f:
        f.write(new_content)

clean_imports('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt')
clean_imports('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/AboutScreen.kt')
