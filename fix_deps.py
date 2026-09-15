import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

if "vico" not in content:
    deps = """
  implementation("com.patrykandpatrick.vico:compose:1.14.0")
  implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")
  implementation("com.patrykandpatrick.vico:core:1.14.0")
"""
    content = content.replace("implementation(libs.androidx.compose.ui.tooling.preview)", "implementation(libs.androidx.compose.ui.tooling.preview)\n" + deps)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
