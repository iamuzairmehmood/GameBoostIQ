with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# Add profileColorHex property usages to the view
old_box = """                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (game.performanceMode) {
                                    "PERFORMANCE" -> ZoneRed.copy(alpha = 0.15f)
                                    "BALANCED" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else -> StatSupported.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = game.performanceMode,
                            color = when (game.performanceMode) {
                                "PERFORMANCE" -> ZoneRed
                                "BALANCED" -> MaterialTheme.colorScheme.primary
                                else -> StatSupported
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }"""
                    
# Wait, let's extract the color in the LaunchedEffect instead
# I will rewrite the GameItemCard to support Palette extraction and profile color.

# We need imports:
imports = """import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.palette.graphics.Palette"""
if "androidx.palette.graphics.Palette" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", imports + "\\nimport androidx.compose.runtime.Composable")

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
