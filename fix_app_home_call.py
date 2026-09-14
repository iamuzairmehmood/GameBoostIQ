import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

pattern = r'composable\("home"\)\s*\{[\s\S]*?onLaunchGame = \{[\s\S]*?\}\n\s*\)'
# I'll just find the exact block and replace it. Let's use simple string replacement from a known anchor.
start_idx = content.find('composable("home") {')
end_idx = content.find('composable("game_library") {')

if start_idx != -1 and end_idx != -1:
    before = content[:start_idx]
    after = content[end_idx:]
    
    new_home = """composable("home") {
                        HomeScreen(
                            stats = stats,
                            isGamingActive = isGamingActive,
                            onStartGaming = { mode, overlay -> },
                            onStopGaming = {
                                scope.launch {
                                    app.gamingModeManager.stopGamingMode()
                                    Toast.makeText(context, "Gaming Mode Stopped", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNavigate = { route -> navController.navigate(route) },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }
                    """
    content = before + new_home + after
    with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
        f.write(content)
