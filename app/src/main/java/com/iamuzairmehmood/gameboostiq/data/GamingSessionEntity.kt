package com.iamuzairmehmood.gameboostiq.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gaming_sessions")
data class GamingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gamePackageName: String? = null,
    val gameName: String = "Global Gaming Mode",
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Long,
    val avgFps: Float = 0f,
    val minFps: Float = 0f,
    val maxFps: Float = 0f,
    val avgTemp: Float = 0f,
    val maxTemp: Float = 0f,
    val avgPingMs: Int = 0,
    val batteryUsedPercent: Int = 0,
    val startBattery: Int = 100,
    val endBattery: Int = 100,
    val performanceMode: String = "PERFORMANCE",
    // New fields for spec
    val avgJitterMs: Int = 0,
    val packetLossPercent: Float = 0f,
    val wifiDataUsedBytes: Long = 0,
    val mobileDataUsedBytes: Long = 0,
    val totalDataUsedBytes: Long = 0,
    val fpsGreenDurationSec: Long = 0,
    val fpsOrangeDurationSec: Long = 0,
    val fpsRedDurationSec: Long = 0,
    val cpuGreenDurationSec: Long = 0,
    val cpuOrangeDurationSec: Long = 0,
    val cpuRedDurationSec: Long = 0
)
