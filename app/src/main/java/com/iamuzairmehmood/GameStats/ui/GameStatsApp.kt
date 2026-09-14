package com.iamuzairmehmood.GameStats.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iamuzairmehmood.GameStats.GameStatsApplication
import com.iamuzairmehmood.GameStats.data.SettingsRepository
import com.iamuzairmehmood.GameStats.data.GameProfileEntity
import com.iamuzairmehmood.GameStats.model.PerformanceMode
import com.iamuzairmehmood.GameStats.ui.theme.GameStatsTheme
import com.iamuzairmehmood.GameStats.ui.theme.ThemeAccent
import com.iamuzairmehmood.GameStats.ui.theme.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun GameStatsApp() {
    val context = LocalContext.current
    val app = context.applicationContext as GameStatsApplication
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentThemeMode by app.settingsRepository.themeMode.collectAsState()
    val currentThemeAccent by app.settingsRepository.themeAccent.collectAsState()
    val isDynamicColor by app.settingsRepository.isDynamicColor.collectAsState()
    val hasCompletedSetup by app.settingsRepository.hasCompletedSetup.collectAsState()

    val stats by app.performanceMonitor.liveStats.collectAsState()
    val isGamingActive by app.gamingModeManager.isGamingModeActive.collectAsState()
    val selectedMode by app.gamingModeManager.activePerformanceMode.collectAsState()
    val latestRestoreReport by app.gamingModeManager.latestRestoreReport.collectAsState()

    val games by app.repository.gameProfiles.collectAsState(initial = emptyList())
    val sessions by app.repository.allSessions.collectAsState(initial = emptyList())
    val deviceReport = remember { app.capabilityScanner.performFullScan() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    GameStatsTheme(
        themeMode = currentThemeMode,
        themeAccent = currentThemeAccent,
        dynamicColor = isDynamicColor
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "GameStats",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider()
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("home")
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Game Library") },
                        selected = currentRoute == "game_library",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("game_library")
                        },
                        icon = { Icon(Icons.Default.VideogameAsset, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Sessions & Stats") },
                        selected = currentRoute == "session_stats",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("session_stats")
                        },
                        icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = currentRoute == "settings",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("settings")
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Theme") },
                        selected = currentRoute == "theme",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("theme")
                        },
                        icon = { Icon(Icons.Default.ColorLens, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("Privacy & Security") },
                        selected = currentRoute == "privacy",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("privacy")
                        },
                        icon = { Icon(Icons.Default.Security, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text("About") },
                        selected = currentRoute == "about",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("about")
                        },
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    Spacer(Modifier.weight(1f))
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "GameStats",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("http://iamuzairmehmood.github.io/"))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }) {
                            Text(
                                text = "Developed by ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "iamuzairmehmood",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = if (hasCompletedSetup) "home" else "onboarding"
                ) {
                    composable("home") {
                        HomeScreen(
                            stats = stats,
                            isGamingActive = isGamingActive,
                            recentApps = games.sortedByDescending { it.lastPlayedTimestamp }.take(3),
                            sessions = sessions,
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
                    composable("game_library") {
                        GameLibraryScreen(
                            games = games,
                            onBack = { navController.popBackStack() },
                            onLaunchGame = { game ->
                                val mode = when (game.performanceMode) {
                                    "BALANCED" -> PerformanceMode.BALANCED
                                    "BATTERY_SAVER" -> PerformanceMode.BATTERY_SAVER
                                    else -> PerformanceMode.PERFORMANCE
                                }
                                app.gamingModeManager.startGamingMode(
                                    mode = mode,
                                    gameName = game.appName,
                                    enableOverlay = game.fpsOverlayEnabled || game.tempOverlayEnabled || game.pingOverlayEnabled
                                )
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(game.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                } else {
                                    Toast.makeText(context, "Gaming Mode engaged for ${game.appName}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onUpdateGame = { updated ->
                                scope.launch {
                                    app.repository.updateGameProfile(updated)
                                }
                            },
                            onDeleteGame = { pkg ->
                                scope.launch {
                                    app.repository.deleteGameProfile(pkg)
                                }
                            },
                            onAddGame = { pkg, name ->
                                scope.launch {
                                    app.repository.insertGameProfile(
                                        GameProfileEntity(
                                            packageName = pkg,
                                            appName = name,
                                            isCustomAdded = true,
                                            performanceMode = "PERFORMANCE"
                                        )
                                    )
                                }
                            },
                            onRescan = {
                                scope.launch {
                                    app.repository.scanAndSeedInstalledGames()
                                    Toast.makeText(context, "Scanned installed games", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    composable("capability_scanner") {
                        CapabilityScannerScreen(
                            report = deviceReport,
                            onBack = { navController.popBackStack() },
                            onRescan = {
                                Toast.makeText(context, "Audit refreshed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    composable("tools") {
                        ToolsScreen(
                            stats = stats,
                            deviceReport = deviceReport,
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                    composable("permission_center") {
                        PermissionCenterScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            currentThemeMode = currentThemeMode,
                            currentThemeAccent = currentThemeAccent,
                            isDynamicColor = isDynamicColor,
                            onThemeModeChange = { app.settingsRepository.setThemeMode(it) },
                            onThemeAccentChange = { app.settingsRepository.setThemeAccent(it) },
                            onDynamicColorChange = { app.settingsRepository.setDynamicColor(it) },
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                    composable("theme") {
                        SettingsScreen(
                            currentThemeMode = currentThemeMode,
                            currentThemeAccent = currentThemeAccent,
                            isDynamicColor = isDynamicColor,
                            onThemeModeChange = { app.settingsRepository.setThemeMode(it) },
                            onThemeAccentChange = { app.settingsRepository.setThemeAccent(it) },
                            onDynamicColorChange = { app.settingsRepository.setDynamicColor(it) },
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) }
                        )
                    }
                    composable("privacy") {
                        PrivacyScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("session_stats") {
                        SessionStatsScreen(
                            sessions = sessions,
                            onBack = { navController.popBackStack() },
                            onClearHistory = {
                                scope.launch {
                                    app.repository.clearSessionHistory()
                                    Toast.makeText(context, "Session history cleared", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    composable("about") {
                        AboutScreen(
                            deviceReport = deviceReport,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("onboarding") {
                        OnboardingScreen(
                            deviceReport = deviceReport,
                            onComplete = {
                                app.settingsRepository.setCompletedSetup(true)
                                navController.navigate("home") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
