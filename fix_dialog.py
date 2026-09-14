import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

old_dialog_start = """@Composable
private fun GameProfileDialog(
    profile: GameProfileEntity,
    onDismiss: () -> Unit,
    onSave: (GameProfileEntity) -> Unit
) {
    var mode by remember { mutableStateOf(profile.performanceMode) }
    var dnd by remember { mutableStateOf(profile.dndEnabled) }
    var overlayFps by remember { mutableStateOf(profile.fpsOverlayEnabled) }
    var overlayTemp by remember { mutableStateOf(profile.tempOverlayEnabled) }
    var overlayPing by remember { mutableStateOf(profile.pingOverlayEnabled) }
    var bgOpt by remember { mutableStateOf(profile.backgroundOptimizationEnabled) }"""
    
new_dialog_start = """@Composable
private fun GameProfileDialog(
    profile: GameProfileEntity,
    onDismiss: () -> Unit,
    onSave: (GameProfileEntity) -> Unit
) {
    var mode by remember { mutableStateOf(profile.performanceMode) }
    var dnd by remember { mutableStateOf(profile.dndEnabled) }
    var overlayFps by remember { mutableStateOf(profile.fpsOverlayEnabled) }
    var overlayTemp by remember { mutableStateOf(profile.tempOverlayEnabled) }
    var overlayPing by remember { mutableStateOf(profile.pingOverlayEnabled) }
    var bgOpt by remember { mutableStateOf(profile.backgroundOptimizationEnabled) }
    var selectedColorHex by remember { mutableStateOf(profile.profileColorHex) }"""

content = content.replace(old_dialog_start, new_dialog_start)


old_confirm = """            TextButton(onClick = {
                onSave(
                    profile.copy(
                        performanceMode = mode,
                        dndEnabled = dnd,
                        fpsOverlayEnabled = overlayFps,
                        tempOverlayEnabled = overlayTemp,
                        pingOverlayEnabled = overlayPing,
                        backgroundOptimizationEnabled = bgOpt
                    )
                )
            }) {"""
            
new_confirm = """            TextButton(onClick = {
                onSave(
                    profile.copy(
                        performanceMode = mode,
                        dndEnabled = dnd,
                        fpsOverlayEnabled = overlayFps,
                        tempOverlayEnabled = overlayTemp,
                        pingOverlayEnabled = overlayPing,
                        backgroundOptimizationEnabled = bgOpt,
                        profileColorHex = selectedColorHex
                    )
                )
            }) {"""

content = content.replace(old_confirm, new_confirm)


color_picker_ui = """
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Profile Theme Color",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        null to "Auto",
                        "#E53935" to "Red",
                        "#43A047" to "Green",
                        "#1E88E5" to "Blue",
                        "#FB8C00" to "Orange",
                        "#8E24AA" to "Purple"
                    )
                    
                    colors.forEach { (hex, name) ->
                        val isSelected = selectedColorHex == hex
                        val displayColor = if (hex != null) Color(android.graphics.Color.parseColor(hex)) else MaterialTheme.colorScheme.onSurfaceVariant
                        
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (hex != null) displayColor else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (hex == null) {
                                Text("A", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = displayColor)
                            } else if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
"""

# Insert before "Floating Overlay Metrics"
target = """                Text(
                    text = "Floating Overlay Metrics","""
content = content.replace(target, color_picker_ui + target)


with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
