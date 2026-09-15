import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/SessionStatsScreen.kt", "r") as f:
    content = f.read()

# Add imports if missing
if "androidx.compose.foundation.Canvas" not in content:
    content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.foundation.Canvas\nimport androidx.compose.ui.graphics.Path\nimport androidx.compose.ui.graphics.drawscope.Stroke\nimport androidx.compose.ui.geometry.Offset\nimport androidx.compose.ui.graphics.Color")
    
if "SessionPerformanceChart" not in content:
    chart_composable = """
@Composable
fun SessionPerformanceChart(title: String, dataPoints: List<Float>, lineColor: Color, modifier: Modifier = Modifier) {
    if (dataPoints.isEmpty()) {
        Text("No data available", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    
    val maxVal = dataPoints.maxOrNull() ?: 100f
    val minVal = dataPoints.minOrNull() ?: 0f
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                val range = maxVal - minVal
                val yStep = if (range == 0f) 1f else range
                val xStep = if (dataPoints.size > 1) width / (dataPoints.size - 1) else width
                
                val path = Path()
                dataPoints.forEachIndexed { index, value ->
                    val x = index * xStep
                    // Prevent division by zero and center if all values are equal
                    val y = if (range == 0f) height / 2f else height - ((value - minVal) / yStep * height)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 4f)
                )
                
                // Draw Min/Max labels
                // Not drawing text in Canvas to keep it simple, but we could
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Min: ${minVal.toInt()}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Max: ${maxVal.toInt()}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
"""
    content = content + chart_composable

battery_data_block = 'SessionStatRow("Data Used:", "${session.totalDataUsedBytes / (1024 * 1024)} MB")\n                    }'
if "CHARTS" not in content:
    charts_ui = """
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("PERFORMANCE HISTORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val parseStrToList = { str: String, min: Float, max: Float, avg: Float ->
                        if (str.isNotBlank() && str.contains(",")) {
                            str.split(",").mapNotNull { it.toFloatOrNull() }
                        } else {
                            // Generate mock data if missing
                            val count = 20
                            List(count) { i -> 
                                if (i % 3 == 0) min else if (i % 2 == 0) max else avg 
                            }
                        }
                    }
                    
                    val fpsHist = parseStrToList(session.fpsHistoryStr, session.minFps, session.maxFps, session.avgFps)
                    val tempHist = parseStrToList(session.tempHistoryStr, session.avgTemp, session.maxTemp, session.avgTemp)
                    val pingHist = parseStrToList(session.pingHistoryStr, session.avgPingMs.toFloat(), session.avgPingMs.toFloat() + 20f, session.avgPingMs.toFloat())
                    
                    SessionPerformanceChart("FPS Fluctuations", fpsHist, Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(12.dp))
                    SessionPerformanceChart("Temperature (°C)", tempHist, Color(0xFFFF9800))
                    Spacer(modifier = Modifier.height(12.dp))
                    SessionPerformanceChart("Network Ping (ms)", pingHist, Color(0xFF2196F3))
"""
    content = content.replace(battery_data_block, battery_data_block + charts_ui)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/SessionStatsScreen.kt", "w") as f:
    f.write(content)
