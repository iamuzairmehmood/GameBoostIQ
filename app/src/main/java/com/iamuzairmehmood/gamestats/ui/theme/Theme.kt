package com.iamuzairmehmood.gamestats.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class ThemeAccent {
    GREEN,
    BLUE,
    PURPLE,
    ORANGE,
    RED
}

private fun getDarkColorScheme(accent: ThemeAccent): ColorScheme {
    val primaryColor = when (accent) {
        ThemeAccent.GREEN -> EmeraldGreen
        ThemeAccent.BLUE -> OceanBlue
        ThemeAccent.PURPLE -> RoyalPurple
        ThemeAccent.ORANGE -> BlazeOrange
        ThemeAccent.RED -> CrimsonRed
    }
    
    return darkColorScheme(
        primary = primaryColor,
        onPrimary = Color(0xFF101318), // Dark contrast
        primaryContainer = primaryColor.copy(alpha = 0.2f),
        onPrimaryContainer = primaryColor,
        secondary = primaryColor.copy(alpha = 0.8f),
        onSecondary = Color(0xFF101318),
        background = DarkBackground,
        onBackground = TextPrimaryDark,
        surface = DarkSurface,
        onSurface = TextPrimaryDark,
        surfaceVariant = DarkCard,
        onSurfaceVariant = TextSecondaryDark,
        outline = DarkBorder,
        outlineVariant = Color(0xFF1E2838),
        error = CrimsonRed,
        onError = Color.White
    )
}

private fun getLightColorScheme(accent: ThemeAccent): ColorScheme {
    val primaryColor = when (accent) {
        ThemeAccent.GREEN -> EmeraldGreen
        ThemeAccent.BLUE -> OceanBlue
        ThemeAccent.PURPLE -> RoyalPurple
        ThemeAccent.ORANGE -> BlazeOrange
        ThemeAccent.RED -> CrimsonRed
    }

    return lightColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = primaryColor.copy(alpha = 0.2f),
        onPrimaryContainer = primaryColor,
        secondary = primaryColor.copy(alpha = 0.8f),
        onSecondary = Color.White,
        background = LightBackground,
        onBackground = TextPrimaryLight,
        surface = LightSurface,
        onSurface = TextPrimaryLight,
        surfaceVariant = LightCard,
        onSurfaceVariant = TextSecondaryLight,
        outline = LightBorder,
        outlineVariant = Color(0xFFE2E8F0),
        error = CrimsonRed,
        onError = Color.White
    )
}

@Composable
fun GameStatsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    themeAccent: ThemeAccent = ThemeAccent.GREEN,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> getDarkColorScheme(themeAccent)
        else -> getLightColorScheme(themeAccent)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun GameStatsTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GameStatsTheme(themeMode = themeMode, dynamicColor = dynamicColor, content = content)
}

@Composable
fun GameStatsTheme(content: @Composable () -> Unit) {
    GameStatsTheme(themeMode = ThemeMode.DARK, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    GameStatsTheme(
        themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
        dynamicColor = dynamicColor,
        content = content
    )
}
