package com.iamuzairmehmood.GameStats.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iamuzairmehmood.GameStats.model.LiveGamingStats
import com.iamuzairmehmood.GameStats.model.PerformanceMode
import com.iamuzairmehmood.GameStats.data.GameProfileEntity
import com.iamuzairmehmood.GameStats.data.GamingSessionEntity
import com.iamuzairmehmood.GameStats.service.OverlayService
import com.iamuzairmehmood.GameStats.monitor.PingTester
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    stats: LiveGamingStats,
    isGamingActive: Boolean,
    recentApps: List<GameProfileEntity> = emptyList(),
    sessions: List<GamingSessionEntity> = emptyList(),
    onStartGaming: (PerformanceMode, Boolean) -> Unit,
    onStopGaming: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    var hudEnabled by remember { mutableStateOf(stats.isOverlayActive) }
    val hasOverlayPermission = Settings.canDrawOverlays(context)
    val scope = rememberCoroutineScope()
    
    val pingTester = remember { PingTester() }
    val pingResult by pingTester.testResult.collectAsState()
    var selectedDuration by remember { mutableStateOf(10) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GameStats", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isGamingActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GAME MODE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isGamingActive) "ON" else "OFF",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isGamingActive && stats.activeGameName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Target: ${stats.activeGameName}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Primary Action
            Button(
                onClick = {
                    if (isGamingActive) {
                        onStopGaming()
                    } else {
                        onNavigate("game_library")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isGamingActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isGamingActive) "STOP GAMING" else "START GAMING",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // HUD Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("HUD Overlay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            if (hasOverlayPermission) "Real-time overlay on screen" else "Requires 'Display over other apps' permission", 
                            fontSize = 12.sp, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = hudEnabled,
                        onCheckedChange = { checked ->
                            if (checked && !hasOverlayPermission) {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                                context.startActivity(intent)
                            } else {
                                hudEnabled = checked
                                if (checked) {
                                    val intent = Intent(context, OverlayService::class.java)
                                    intent.putExtra("FPS_ENABLED", true)
                                    intent.putExtra("TEMP_ENABLED", true)
                                    intent.putExtra("PING_ENABLED", true)
                                    context.startService(intent)
                                } else {
                                    context.stopService(Intent(context, OverlayService::class.java))
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Top Apps / Quick Launch
            if (recentApps.isNotEmpty()) {
                Text(
                    "Quick Launch",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentApps) { app ->
                        TopAppItem(
                            app = app,
                            onClick = {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            // Live Performance Monitor
            Text(
                "Live Performance Monitor",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        PerformanceMetric("FPS", stats.fps?.toString() ?: "--", Icons.Default.Speed)
                        PerformanceMetric("CPU", if (stats.cpuLoadPercent != null) "${stats.cpuLoadPercent}%" else "Unavail", Icons.Default.Memory)
                        PerformanceMetric("GPU", "Unsupported", Icons.Default.DeveloperBoard)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        PerformanceMetric("RAM", "${stats.ramUsedGb} GB", Icons.Default.Storage)
                        PerformanceMetric("TEMP", "${stats.temperatureCelsius}°C", Icons.Default.Thermostat)
                        PerformanceMetric("PING", "${stats.pingMs} ms", Icons.Default.NetworkPing)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        PerformanceMetric("BATT", "${stats.batteryPercent}%", Icons.Default.BatteryFull)
                        PerformanceMetric("NET", stats.networkType, Icons.Default.Wifi)
                        PerformanceMetric("HZ", "${stats.currentRefreshRateHz.toInt()} Hz", Icons.Default.Refresh)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            // Last Game Stats
            Text(
                "Last Game Stats",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (sessions.isNotEmpty()) {
                val lastSession = sessions.maxByOrNull { it.endTime ?: it.startTime }!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VideogameAsset, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(lastSession.gameName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        val dateString = formatter.format(Date(lastSession.startTime))
                        Text("Played on: $dateString", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        val durationMins = lastSession.durationSeconds / 60
                        Text("Duration: ${durationMins}m", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PingResultMetric("Avg FPS", if (lastSession.avgFps.toInt() > 0) "${lastSession.avgFps.toInt()}" else "--")
                            PingResultMetric("Avg Ping", if (lastSession.avgPingMs > 0) "${lastSession.avgPingMs}ms" else "--")
                            PingResultMetric("Max Temp", if (lastSession.maxTemp.toInt() > 0) "${lastSession.maxTemp.toInt()}°C" else "--")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { onNavigate("session_stats") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View All Sessions")
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No sessions recorded yet.", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Play a game with Game Mode to see stats.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Ping Test Section
            Text(
                "Network Ping Test",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Target: 8.8.8.8 (Google DNS)", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (!pingResult.isRunning && pingResult.progressPercent == 0f) {
                        // Configuration State
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf(10, 20, 30, 60).forEach { dur ->
                                FilterChip(
                                    selected = selectedDuration == dur,
                                    onClick = { selectedDuration = dur },
                                    label = { Text("${dur}s") }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { scope.launch { pingTester.runPingTest(selectedDuration) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Start Ping Test")
                        }
                    } else {
                        // Running or Finished State
                        if (pingResult.isRunning) {
                            LinearProgressIndicator(
                                progress = { pingResult.progressPercent },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Testing... ${pingResult.timeRemainingSec}s remaining", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            Button(
                                onClick = { scope.launch { pingTester.runPingTest(selectedDuration) } },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Restart Ping Test")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PingResultMetric("Min", if (pingResult.minPing > 0) "${pingResult.minPing}ms" else "--")
                            PingResultMetric("Avg", if (pingResult.avgPing > 0) "${pingResult.avgPing}ms" else "--")
                            PingResultMetric("Max", if (pingResult.maxPing > 0) "${pingResult.maxPing}ms" else "--")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PingResultMetric("Loss", "${pingResult.packetLossPercent}%")
                            PingResultMetric("Jitter", "${pingResult.jitterMs}ms")
                            PingResultMetric("Fail", "${pingResult.failedCount}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PerformanceMetric(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun PingResultMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}


@Composable
fun TopAppItem(app: GameProfileEntity, onClick: () -> Unit) {
    val context = LocalContext.current
    var iconBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    
    LaunchedEffect(app.packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(app.packageName)
            iconBitmap = drawable.toBitmap().asImageBitmap()
        } catch (e: Exception) {
            // fallback
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp).clickable { onClick() }
    ) {
        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap!!,
                contentDescription = app.appName,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.VideogameAsset, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = app.appName,
            fontSize = 11.sp,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
