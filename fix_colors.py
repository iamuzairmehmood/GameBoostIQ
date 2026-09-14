import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

# Make sure imports are present
imports_palette = """import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.palette.graphics.Palette
"""
if "import androidx.palette.graphics.Palette" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", imports_palette + "\\nimport androidx.compose.runtime.Composable")

# Find the GameItemCard block
old_card_start = """@Composable
fun GameItemCard(
    game: GameProfileEntity,
    onLaunch: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            var iconBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
            
            LaunchedEffect(game.packageName) {
                try {
                    val pm = context.packageManager
                    val drawable = pm.getApplicationIcon(game.packageName)
                    iconBitmap = drawable.toBitmap().asImageBitmap()
                } catch (e: Exception) {
                }
            }"""

new_card_start = """@Composable
fun GameItemCard(
    game: GameProfileEntity,
    onLaunch: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var iconBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var extractedColor by remember { mutableStateOf<Color?>(null) }

    val defaultFallback = MaterialTheme.colorScheme.primary
    val displayColor = remember(game.profileColorHex, extractedColor) {
        if (game.profileColorHex != null) {
            try {
                Color(android.graphics.Color.parseColor(game.profileColorHex))
            } catch(e: Exception) {
                extractedColor ?: defaultFallback
            }
        } else {
            extractedColor ?: defaultFallback
        }
    }

    LaunchedEffect(game.packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(game.packageName)
            val bmp = drawable.toBitmap()
            iconBitmap = bmp.asImageBitmap()
            
            Palette.from(bmp).generate { palette ->
                palette?.dominantSwatch?.rgb?.let { colorInt ->
                    extractedColor = Color(colorInt)
                } ?: palette?.vibrantSwatch?.rgb?.let { colorInt ->
                    extractedColor = Color(colorInt)
                }
            }
        } catch (e: Exception) {
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, displayColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {"""
content = content.replace(old_card_start, new_card_start)


# Change the play button color to displayColor
old_play_btn = """            Button(
                onClick = onLaunch,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.height(38.dp)
            ) {"""
new_play_btn = """            Button(
                onClick = onLaunch,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = displayColor,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(38.dp)
            ) {"""
content = content.replace(old_play_btn, new_play_btn)


# Replace performance mode indicator background
old_perf_box = """                    Box(
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
                    ) {"""
new_perf_box = """                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (game.performanceMode) {
                                    "PERFORMANCE" -> ZoneRed.copy(alpha = 0.15f)
                                    "BALANCED" -> displayColor.copy(alpha = 0.15f)
                                    else -> StatSupported.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {"""
content = content.replace(old_perf_box, new_perf_box)

# Replace performance mode indicator text color
old_perf_text = """                        Text(
                            text = game.performanceMode,
                            color = when (game.performanceMode) {
                                "PERFORMANCE" -> ZoneRed
                                "BALANCED" -> MaterialTheme.colorScheme.primary
                                else -> StatSupported
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )"""
new_perf_text = """                        Text(
                            text = game.performanceMode,
                            color = when (game.performanceMode) {
                                "PERFORMANCE" -> ZoneRed
                                "BALANCED" -> displayColor
                                else -> StatSupported
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )"""
content = content.replace(old_perf_text, new_perf_text)


with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
