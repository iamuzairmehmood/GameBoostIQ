import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Make sure imports are present for Date/Time formatting and Icons
imports_to_add = """import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
"""
if "import java.text.SimpleDateFormat" not in content:
    content = content.replace("import androidx.compose.animation.AnimatedVisibility", imports_to_add + "\nimport androidx.compose.animation.AnimatedVisibility")


top_apps_code = """
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
"""

last_game_code = """
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
                            Text(lastSession.appName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        val dateString = formatter.format(Date(lastSession.startTime))
                        Text("Played on: $dateString", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        val durationMins = lastSession.durationMinutes
                        Text("Duration: ${durationMins}m", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PingResultMetric("Avg FPS", if (lastSession.averageFps > 0) "${lastSession.averageFps}" else "--")
                            PingResultMetric("Avg Ping", if (lastSession.averagePing > 0) "${lastSession.averagePing}ms" else "--")
                            PingResultMetric("Max Temp", if (lastSession.maxTemperature > 0) "${lastSession.maxTemperature}°C" else "--")
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
"""

# Insert Top Apps before Live Performance Monitor
target1 = """            // Live Performance Monitor"""
content = content.replace(target1, top_apps_code + target1)

# Insert Last Game Stats after Live Performance Monitor
target2 = """            // Ping Test Section"""
content = content.replace(target2, last_game_code + target2)

# Add TopAppItem Composable
top_app_item = """
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
"""

content = content + "\n" + top_app_item

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(content)
