package com.iamuzairmehmood.gameboostiq.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_profiles")
data class GameProfileEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isCustomAdded: Boolean = false,
    val performanceMode: String = "PERFORMANCE",
    val fpsOverlayEnabled: Boolean = true,
    val tempOverlayEnabled: Boolean = true,
    val pingOverlayEnabled: Boolean = true,
    val ramOverlayEnabled: Boolean = true,
    val refreshOverlayEnabled: Boolean = true,
    val batteryOverlayEnabled: Boolean = true,
    val preferredRefreshRate: Float = 0f, // 0 means default/auto
    val dndEnabled: Boolean = true,
    val backgroundOptimizationEnabled: Boolean = true,
    val autoStartGamingMode: Boolean = false,
    val lastPlayedTimestamp: Long = 0L,
    val totalPlayTimeSeconds: Long = 0L
)
