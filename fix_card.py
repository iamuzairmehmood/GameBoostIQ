import re
with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    content = f.read()

old_card = """            // Main Status Card
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
            }"""

new_card = """            // Main Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                border = if (isGamingActive) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.5f)) else null,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isGamingActive) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.8f)
                                    )
                                )
                            }
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = if (isGamingActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "GAME MODE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGamingActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isGamingActive) "ACTIVE" else "INACTIVE",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isGamingActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        if (isGamingActive && stats.activeGameName != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha=0.15f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "Target: ${stats.activeGameName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }"""

content = content.replace(old_card, new_card)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(content)
