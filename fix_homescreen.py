import re

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Add Palette import
if "androidx.palette.graphics.Palette" not in content:
    content = content.replace("import androidx.compose.ui.platform.LocalContext", "import androidx.palette.graphics.Palette\nimport androidx.compose.ui.platform.LocalContext")


old_top_app_item = """@Composable
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
}"""

new_top_app_item = """@Composable
fun TopAppItem(app: GameProfileEntity, onClick: () -> Unit) {
    val context = LocalContext.current
    var iconBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    var extractedColor by remember { mutableStateOf<Color?>(null) }

    val defaultFallback = MaterialTheme.colorScheme.primary
    val displayColor = remember(app.profileColorHex, extractedColor) {
        if (app.profileColorHex != null) {
            try {
                Color(android.graphics.Color.parseColor(app.profileColorHex))
            } catch(e: Exception) {
                extractedColor ?: defaultFallback
            }
        } else {
            extractedColor ?: defaultFallback
        }
    }

    LaunchedEffect(app.packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(app.packageName)
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
            // fallback
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp).clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(displayColor.copy(alpha = 0.15f))
                .border(2.dp, displayColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap!!,
                    contentDescription = app.appName,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                )
            } else {
                Icon(
                    Icons.Default.VideogameAsset, 
                    contentDescription = null, 
                    tint = displayColor,
                    modifier = Modifier.size(28.dp)
                )
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
}"""

content = content.replace(old_top_app_item, new_top_app_item)

with open("app/src/main/java/com/iamuzairmehmood/GameStats/ui/HomeScreen.kt", "w") as f:
    f.write(content)
