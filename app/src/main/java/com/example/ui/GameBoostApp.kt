package com.example.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.GameBoostApplication
import com.example.data.GameProfileEntity
import com.example.model.PerformanceMode
import com.example.ui.theme.GameBoostIQTheme
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemeAccent
import kotlinx.coroutines.launch

@Composable
fun GameBoostApp() {
    val context = LocalContext.current
    val app = context.applicationContext as GameBoostApplication
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    var currentThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
    var currentThemeAccent by remember { mutableStateOf(ThemeAccent.GREEN) }
    var isDynamicColor by remember { mutableStateOf(false) }

    val stats by app.performanceMonitor.liveStats.collectAsState()
    val isGamingActive by app.gamingModeManager.isGamingModeActive.collectAsState()
    val selectedMode by app.gamingModeManager.activePerformanceMode.collectAsState()
    val latestRestoreReport by app.gamingModeManager.latestRestoreReport.collectAsState()
    val games by app.repository.gameProfiles.collectAsState(initial = emptyList())
    val sessions by app.repository.allSessions.collectAsState(initial = emptyList())

    val deviceReport = remember { app.capabilityScanner.performFullScan() }

    GameBoostIQTheme(
        themeMode = currentThemeMode,
        themeAccent = currentThemeAccent,
        dynamicColor = isDynamicColor
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        stats = stats,
                        deviceReport = deviceReport,
                        selectedMode = selectedMode,
                        isGamingActive = isGamingActive,
                        games = games,
                        latestRestoreReport = latestRestoreReport,
                        onStartGaming = { mode, enableOverlay ->
                            app.gamingModeManager.startGamingMode(
                                mode = mode,
                                gameName = "Gaming Mode",
                                enableOverlay = enableOverlay
                            )
                        },
                        onStopGaming = {
                            app.gamingModeManager.stopGamingMode()
                        },
                        onDismissRestore = {
                            app.gamingModeManager.dismissRestoreReport()
                        },
                        onSelectMode = { mode ->
                            if (!isGamingActive) {
                                app.gamingModeManager.startGamingMode(mode = mode)
                            }
                        },
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
                                Toast.makeText(
                                    context,
                                    "Ready for ${game.appName}! Launching profile & Gaming Mode.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onNavigate = { route ->
                            navController.navigate(route)
                        }
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

                composable("performance_modes") {
                    PerformanceModesScreen(
                        currentMode = selectedMode,
                        onSelectMode = { mode ->
                            if (isGamingActive) {
                                app.gamingModeManager.stopGamingMode()
                                app.gamingModeManager.startGamingMode(mode = mode)
                            }
                        },
                        onBack = { navController.popBackStack() }
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
                        onThemeModeChange = { currentThemeMode = it },
                        onThemeAccentChange = { currentThemeAccent = it },
                        onDynamicColorChange = { isDynamicColor = it },
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> navController.navigate(route) }
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
