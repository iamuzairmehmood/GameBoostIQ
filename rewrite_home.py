import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Add imports if missing
imports = [
    "import com.iamuzairmehmood.GameStats.data.GamingSessionEntity",
    "import com.iamuzairmehmood.GameStats.data.GameProfileEntity"
]
for imp in imports:
    if imp not in content:
        content = content.replace("import com.iamuzairmehmood.GameStats.model.PerformanceMode", f"import com.iamuzairmehmood.GameStats.model.PerformanceMode\n{imp}")

# Update signature
old_sig = """fun HomeScreen(
    stats: LiveGamingStats,
    isGamingActive: Boolean,
    onStartGaming: (PerformanceMode, Boolean) -> Unit,
    onStopGaming: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {"""

new_sig = """fun HomeScreen(
    stats: LiveGamingStats,
    isGamingActive: Boolean,
    recentApps: List<GameProfileEntity> = emptyList(),
    sessions: List<GamingSessionEntity> = emptyList(),
    onStartGaming: (PerformanceMode, Boolean) -> Unit,
    onStopGaming: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {"""

content = content.replace(old_sig, new_sig)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(content)

