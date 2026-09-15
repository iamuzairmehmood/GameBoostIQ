package com.iamuzairmehmood.GameStats.data

import android.content.Context
import android.content.SharedPreferences
import com.iamuzairmehmood.GameStats.ui.theme.ThemeAccent
import com.iamuzairmehmood.GameStats.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gamestats_settings", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(
        ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _themeAccent = MutableStateFlow(
        ThemeAccent.valueOf(prefs.getString("theme_accent", ThemeAccent.GREEN.name) ?: ThemeAccent.GREEN.name)
    )
    val themeAccent: StateFlow<ThemeAccent> = _themeAccent.asStateFlow()
    
    private val _isDynamicColor = MutableStateFlow(
        prefs.getBoolean("dynamic_color", false)
    )
    val isDynamicColor: StateFlow<Boolean> = _isDynamicColor.asStateFlow()
    
    private val _hasCompletedSetup = MutableStateFlow(
        prefs.getBoolean("has_completed_setup", false)
    )
    val hasCompletedSetup: StateFlow<Boolean> = _hasCompletedSetup.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun setThemeAccent(accent: ThemeAccent) {
        prefs.edit().putString("theme_accent", accent.name).apply()
        _themeAccent.value = accent
    }

    fun setDynamicColor(enabled: Boolean) {
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
        _isDynamicColor.value = enabled
    }
    
    fun setCompletedSetup(completed: Boolean) {
        prefs.edit().putBoolean("has_completed_setup", completed).apply()
        _hasCompletedSetup.value = completed
    }
}
