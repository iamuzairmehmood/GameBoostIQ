with open("app/build.gradle.kts", "r") as f:
    content = f.read()
    
# Remove any bad syntax
content = content.replace("implementation(\"org.burnoutcrew.composereorderable:reorderable:0.9.6\")", "")

# Add it safely to the bottom of the dependencies block
with open("app/build.gradle.kts", "w") as f:
    f.write(content + "\n")
