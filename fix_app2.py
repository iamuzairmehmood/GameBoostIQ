with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameStatsApp.kt", "r") as f:
    content = f.read()

old_call = """                        HomeScreen(
                            stats = stats,
                            isGamingActive = isGamingActive,"""
new_call = """                        HomeScreen(
                            stats = stats,
                            isGamingActive = isGamingActive,
                            recentApps = games.sortedByDescending { it.lastPlayedTimestamp }.take(3),
                            sessions = sessions,"""

content = content.replace(old_call, new_call)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameStatsApp.kt", "w") as f:
    f.write(content)
