with open("app/src/main/res/drawable/ic_launcher_foreground.xml", "r") as f:
    content = f.read()

content = content.replace('android:tint="#00E676"', "")
content = content.replace('@android:color/white', "#00E676")

with open("app/src/main/res/drawable/ic_launcher_foreground.xml", "w") as f:
    f.write(content)
