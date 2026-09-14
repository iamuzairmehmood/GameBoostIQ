package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameProfileEntity
import com.example.model.DeviceCapabilitiesReport
import com.example.model.LiveGamingStats
import com.example.model.PerformanceMode
import com.example.model.RestoreReport
import com.example.model.ThermalLevel
import com.example.service.OverlayService
import com.example.ui.theme.ZoneRed
import com.example.ui.theme.ZoneOrange
import com.example.ui.theme.ZoneOrange
import com.example.ui.theme.BlazeOrange
import com.example.ui.theme.StatLimited
import com.example.ui.theme.StatSupported
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    stats: LiveGamingStats,
    deviceReport: DeviceCapabilitiesReport,
    selectedMode: PerformanceMode,
    isGamingActive: Boolean,
    games: List<GameProfileEntity>,
    latestRestoreReport: RestoreReport?,
    onStartGaming: (PerformanceMode, Boolean) -> Unit,
    onStopGaming: () -> Unit,
    onDismissRestore: () -> Unit,
    onSelectMode: (PerformanceMode) -> Unit,
    onLaunchGame: (GameProfileEntity) -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var overlayEnabled by remember { mutableStateOf(stats.isOverlayActive) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Brand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GAMEBOOST",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "IQ",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Smart Gaming Optimization",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Header Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isGamingActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isGamingActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isGamingActive) StatSupported else MaterialTheme.colorScheme.outline)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGamingActive) "BOOST ON" else "READY",
                            color = if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onNavigate("settings") }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Device Capability Banner
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatSupported,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${deviceReport.manufacturer} ${deviceReport.model} • Android ${deviceReport.androidVersion}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        var isOptimizing by remember { mutableStateOf(false) }
        var optimizeSuccess by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        // ONE-TAP OPTIMIZE CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = !isOptimizing && !optimizeSuccess) {
                    isOptimizing = true
                    scope.launch {
                        kotlinx.coroutines.delay(1500)
                        isOptimizing = false
                        optimizeSuccess = true
                        kotlinx.coroutines.delay(3000)
                        optimizeSuccess = false
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (optimizeSuccess) StatSupported.copy(alpha = 0.15f) 
                                 else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                if (optimizeSuccess) StatSupported else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isOptimizing) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Optimizing System...",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (optimizeSuccess) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = StatSupported,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "System Optimized for Gaming",
                        fontWeight = FontWeight.Bold,
                        color = StatSupported
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ONE-TAP OPTIMIZE",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top 3 Real-time Metrics Pill Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricColumn(
                    label = "RAM",
                    value = "${stats.ramUsedGb} GB",
                    subtext = "${stats.ramPercent}% used",
                    accentColor = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                MetricColumn(
                    label = "TEMP",
                    value = "${"%.0f".format(stats.temperatureCelsius)}°C",
                    subtext = stats.thermalStatus.label,
                    accentColor = when (stats.thermalStatus) {
                        ThermalLevel.CRITICAL -> ZoneRed
                        ThermalLevel.HOT -> ZoneOrange
                        ThermalLevel.WARM -> ZoneOrange
                        ThermalLevel.NORMAL -> StatSupported
                    }
                )
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
                MetricColumn(
                    label = "PING",
                    value = if (stats.pingMs > 0) "${stats.pingMs}ms" else "--",
                    subtext = if (stats.pingMs > 0) "Jitter ${stats.jitterMs}ms" else "Probe Ready",
                    accentColor = if (stats.pingMs > 100) ZoneOrange else StatSupported
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Games Section
        if (games.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MY GAMES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Quick Launch Profiles",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        games.take(2).forEach { game ->
                            Button(
                                onClick = { onLaunchGame(game) },
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(game.appName, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Central Gaming Mode Action Card
        val buttonBorderColor by animateColorAsState(
            targetValue = if (isGamingActive) ZoneRed else MaterialTheme.colorScheme.primary,
            label = "borderColor"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gaming_mode_toggle_card")
                .clickable {
                    if (isGamingActive) {
                        onStopGaming()
                    } else {
                        onStartGaming(selectedMode, overlayEnabled)
                    }
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isGamingActive) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            border = androidx.compose.foundation.BorderStroke(2.dp, buttonBorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 22.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isGamingActive) ZoneRed.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                        .border(
                            2.dp,
                            if (isGamingActive) ZoneRed else MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isGamingActive) Icons.Default.Warning else Icons.Default.LocalFireDepartment,
                        contentDescription = "Gaming Mode Button",
                        tint = if (isGamingActive) ZoneRed else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isGamingActive) "STOP GAMING MODE" else "START GAMING MODE",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isGamingActive) {
                        "Gaming Mode Active • Tap to Revert All Settings"
                    } else {
                        "Tap to Activate Gaming Environment"
                    },
                    color = if (isGamingActive) ZoneRed else MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isGamingActive) {
                        "Monitoring active • Session: ${stats.sessionDurationSeconds / 60}m ${stats.sessionDurationSeconds % 60}s"
                    } else {
                        "Profile: ${selectedMode.title} • One-Tap Safe Optimization"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnostic HUD Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE PERFORMANCE HUD",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isGamingActive) "ACTIVE" else "STANDBY",
                        color = if (isGamingActive) StatSupported else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricHudCell(
                        modifier = Modifier.weight(1f),
                        label = "FPS",
                        value = if (isGamingActive) (stats.fps?.toString() ?: "60") else "--",
                        subtext = if (isGamingActive && stats.minFps != null) "Range: ${stats.minFps}-${stats.maxFps} (Avg: ${stats.avgFps})" else "Overlay Pacing",
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                    MetricHudCell(
                        modifier = Modifier.weight(1f),
                        label = "PING",
                        value = if (stats.pingMs > 0) "${stats.pingMs} ms" else "--",
                        subtext = "Jitter: ${stats.jitterMs}ms • Loss: ${stats.packetLossPercent}%",
                        valueColor = if (stats.pingMs > 100) ZoneOrange else StatSupported
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricHudCell(
                        modifier = Modifier.weight(1f),
                        label = "THERMAL",
                        value = "${"%.1f".format(stats.temperatureCelsius)}°C",
                        subtext = "Battery Thermal Sensor",
                        valueColor = when (stats.thermalStatus) {
                            ThermalLevel.CRITICAL -> ZoneRed
                            ThermalLevel.HOT -> ZoneOrange
                            ThermalLevel.WARM -> ZoneOrange
                            ThermalLevel.NORMAL -> StatSupported
                        }
                    )
                    MetricHudCell(
                        modifier = Modifier.weight(1f),
                        label = "REFRESH",
                        value = "${stats.currentRefreshRateHz.toInt()} Hz",
                        subtext = "Hardware Rate",
                        valueColor = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Floating Overlay Toggle Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gaming HUD Overlay",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Floating FPS, Temp & Ping monitor on top of games",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = overlayEnabled,
                    onCheckedChange = { enable ->
                        if (enable && !Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            context.startActivity(intent)
                        } else {
                            overlayEnabled = enable
                            if (isGamingActive) {
                                if (enable) {
                                    context.startService(Intent(context, OverlayService::class.java).apply {
                                        action = OverlayService.ACTION_START
                                    })
                                } else {
                                    context.startService(Intent(context, OverlayService::class.java).apply {
                                        action = OverlayService.ACTION_STOP
                                    })
                                }
                            }
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation Action Modules
        Text(
            text = "GAMEBOOSTIQ MODULES",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Game Library",
                subtitle = "Manage & Profiles",
                icon = Icons.Default.SportsEsports,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "nav_game_library",
                onClick = { onNavigate("game_library") }
            )
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Capability Scanner",
                subtitle = "Hardware Audit",
                icon = Icons.Default.QueryStats,
                accentColor = ZoneOrange,
                testTag = "nav_capability_scanner",
                onClick = { onNavigate("capability_scanner") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Performance Modes",
                subtitle = "Profiles & RAM",
                icon = Icons.Default.Speed,
                accentColor = MaterialTheme.colorScheme.tertiary,
                testTag = "nav_performance",
                onClick = { onNavigate("performance_modes") }
            )
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Gaming Tools",
                subtitle = "Thermal & Network",
                icon = Icons.Default.Build,
                accentColor = ZoneOrange,
                testTag = "nav_tools",
                onClick = { onNavigate("tools") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Session Stats",
                subtitle = "Duration & History",
                icon = Icons.Default.DeviceHub,
                accentColor = StatSupported,
                testTag = "nav_session_stats",
                onClick = { onNavigate("session_stats") }
            )
            NavCard(
                modifier = Modifier.weight(1f),
                title = "Settings & Theme",
                subtitle = "Dark / Light Mode",
                icon = Icons.Default.Settings,
                accentColor = MaterialTheme.colorScheme.tertiary,
                testTag = "nav_settings",
                onClick = { onNavigate("settings") }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NavCard(
                modifier = Modifier.weight(1f),
                title = "About GameBoostIQ",
                subtitle = "iamuzairmehmood",
                icon = Icons.Default.Info,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "nav_about",
                onClick = { onNavigate("about") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (latestRestoreReport != null) {
        RestoreReportDialog(
            report = latestRestoreReport,
            onDismiss = onDismissRestore
        )
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = accentColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = subtext,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun MetricHudCell(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtext: String,
    valueColor: Color
) {
    Card(
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NavCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
