with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameStatsApp.kt", "r") as f:
    content = f.read()

import_statement = "import androidx.navigation.compose.rememberNavController\nimport com.iamuzairmehmood.GameStats.GameStatsApplication"
if "com.iamuzairmehmood.GameStats.data.SettingsRepository" not in content:
    content = content.replace(import_statement, import_statement + "\nimport com.iamuzairmehmood.GameStats.data.SettingsRepository")

# Remove the mutableStateOf for themes and replace with repository values
old_states = """    var currentThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var currentThemeAccent by remember { mutableStateOf(ThemeAccent.GREEN) }
    var isDynamicColor by remember { mutableStateOf(false) }"""

new_states = """    val currentThemeMode by app.settingsRepository.themeMode.collectAsState()
    val currentThemeAccent by app.settingsRepository.themeAccent.collectAsState()
    val isDynamicColor by app.settingsRepository.isDynamicColor.collectAsState()
    val hasCompletedSetup by app.settingsRepository.hasCompletedSetup.collectAsState()"""

content = content.replace(old_states, new_states)

# Fix startDestination
old_nav = """                NavHost(
                    navController = navController,
                    startDestination = "home"
                )"""
new_nav = """                NavHost(
                    navController = navController,
                    startDestination = if (hasCompletedSetup) "home" else "onboarding"
                )"""

content = content.replace(old_nav, new_nav)

# Fix the Settings callback to call repository
old_settings = """                            onThemeModeChange = { currentThemeMode = it },
                            onThemeAccentChange = { currentThemeAccent = it },
                            onDynamicColorChange = { isDynamicColor = it },"""
new_settings = """                            onThemeModeChange = { app.settingsRepository.setThemeMode(it) },
                            onThemeAccentChange = { app.settingsRepository.setThemeAccent(it) },
                            onDynamicColorChange = { app.settingsRepository.setDynamicColor(it) },"""

content = content.replace(old_settings, new_settings)

# Fix onboarding complete callback
old_onboarding = """                            onComplete = {
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }"""
new_onboarding = """                            onComplete = {
                                app.settingsRepository.setCompletedSetup(true)
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }"""

content = content.replace(old_onboarding, new_onboarding)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameStatsApp.kt", "w") as f:
    f.write(content)

