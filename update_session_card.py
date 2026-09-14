import re

with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/SessionStatsScreen.kt", "r") as f:
    content = f.read()

# We need to add an import for expand/collapse, but it's easier to just rewrite SessionCard
card_start = content.find('@Composable\nprivate fun SessionCard')
if card_start != -1:
    content = content[:card_start]

new_card = """
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.clickable

@Composable
private fun SessionCard(session: GamingSessionEntity) {
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.getDefault())
    val dateStr = dateFormat.format(java.util.Date(session.startTime))
    val min = session.durationSeconds / 60
    val sec = session.durationSeconds % 60
    
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VideogameAsset, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.gameName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$dateStr • Duration: ${min}m ${sec}s",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    Icons.Default.ArrowDropDown, 
                    contentDescription = "Expand", 
                    modifier = Modifier.rotate(rotation)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("OVERVIEW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("Mode:", session.performanceMode)
                        SessionStatRow("Start Batt:", "${session.startBattery}%")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("FPS & CPU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("Avg FPS:", "${session.avgFps.toInt()}")
                        SessionStatRow("Min/Max:", "${session.minFps.toInt()} / ${session.maxFps.toInt()}")
                    }
                    val totalFpsSec = (session.fpsGreenDurationSec + session.fpsOrangeDurationSec + session.fpsRedDurationSec).coerceAtLeast(1L)
                    val fpsGreenPct = (session.fpsGreenDurationSec * 100 / totalFpsSec).toInt()
                    val fpsOrangePct = (session.fpsOrangeDurationSec * 100 / totalFpsSec).toInt()
                    val fpsRedPct = (session.fpsRedDurationSec * 100 / totalFpsSec).toInt()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("FPS Zones:", "G:${fpsGreenPct}% O:${fpsOrangePct}% R:${fpsRedPct}%")
                        SessionStatRow("CPU Info:", "Unavailable")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("NETWORK & THERMAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("Avg Ping:", "${session.avgPingMs} ms")
                        SessionStatRow("Avg Temp:", "${"%.1f".format(session.avgTemp)}°C")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("Jitter:", "${session.avgJitterMs} ms")
                        SessionStatRow("Max Temp:", "${"%.1f".format(session.maxTemp)}°C")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BATTERY & DATA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        SessionStatRow("Batt Drain:", "${session.batteryUsedPercent}%")
                        SessionStatRow("Data Used:", "${session.totalDataUsedBytes / (1024 * 1024)} MB")
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionStatRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(75.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}
"""

with open("app/src/main/java/com/iamuzairmehmood/gamestats/ui/SessionStatsScreen.kt", "w") as f:
    # Need to add imports manually if missing, but let's just prepend to the file
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\nimport androidx.compose.animation.AnimatedVisibility\nimport androidx.compose.animation.core.animateFloatAsState\nimport androidx.compose.ui.draw.rotate\nimport androidx.compose.foundation.clickable\nimport androidx.compose.material.icons.filled.ArrowDropDown")
    f.write(content + new_card)

