with open("app/build.gradle.kts", "r") as f:
    content = f.read()
if "androidx.palette" not in content:
    content = content.replace("dependencies {", "dependencies {\n  implementation(\"androidx.palette:palette-ktx:1.0.0\")")
with open("app/build.gradle.kts", "w") as f:
    f.write(content)
