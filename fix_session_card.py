import re

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/SessionStatsScreen.kt', 'r') as f:
    content = f.read()

replacement = """@Composable
private fun SessionCard(session: GamingSessionEntity) {
    val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.getDefault())
    val dateStr = dateFormat.format(java.util.Date(session.startTime))
    val min = session.durationSeconds / 60
    val sec = session.durationSeconds % 60

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Column {
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
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    SessionStatRow("Avg FPS:", "${session.avgFps.toInt()}")
                    SessionStatRow("Low FPS:", "${session.minFps.toInt()}")
                    SessionStatRow("Avg Ping:", "${session.avgPingMs}ms")
                }
                Column {
                    SessionStatRow("Max Temp:", "${"%.1f".format(session.maxTemp)}°C")
                    SessionStatRow("Data:", "${session.totalDataUsedBytes / (1024 * 1024)}MB")
                    SessionStatRow("Battery:", "-${session.batteryUsedPercent}%")
                }
            }
        }
    }
}

@Composable
private fun SessionStatRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(70.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}"""

content = re.sub(r'@Composable\nprivate fun SessionCard\(session: GamingSessionEntity\) \{[\s\S]*', replacement, content)

with open('app/src/main/java/com/iamuzairmehmood/gameboostiq/ui/SessionStatsScreen.kt', 'w') as f:
    f.write(content)
