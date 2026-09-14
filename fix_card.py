import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "r") as f:
    content = f.read()

old_card = """@Composable
private fun GameItemCard(
    game: GameProfileEntity,
    onLaunch: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isFreeFireTitle = game.packageName.contains("dts.freefireth") || game.packageName.contains("dts.freefiremax")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("game_item_${game.packageName}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            if (isFreeFireTitle) 1.5.dp else 1.dp,
            if (isFreeFireTitle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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

new_card = """@Composable
private fun GameItemCard(
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
    
    val isFreeFireTitle = game.packageName.contains("dts.freefireth") || game.packageName.contains("dts.freefiremax")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("game_item_${game.packageName}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp, displayColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {"""

content = content.replace(old_card, new_card)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/GameLibraryScreen.kt", "w") as f:
    f.write(content)
