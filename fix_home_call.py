import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'r') as f:
    content = f.read()

pattern = r'composable\("home"\) \{.*?\}\n                    \}\n                    composable\("game_library"\) \{'

replacement = """composable("home") {
                        HomeScreen(
                            stats = stats,
                            isGamingActive = isGamingActive,
                            onStartGaming = { mode, overlay ->
                                // Not used directly from home anymore, navigates to library
                            },
                            onStopGaming = {
                                scope.launch {
                                    app.gamingModeManager.stopGamingMode()
                                    Toast.makeText(context, "Gaming Mode Stopped", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onNavigate = { route ->
                                navController.navigate(route)
                            },
                            onOpenDrawer = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                    composable("game_library") {"""

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/GameBoostApp.kt', 'w') as f:
    f.write(new_content)
