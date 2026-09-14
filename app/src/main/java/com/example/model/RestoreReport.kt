package com.example.model

data class RestoreReport(
    val sessionDurationSeconds: Long,
    val restoredSettingsCount: Int,
    val restoredSettings: List<String>,
    val manualRestorationCount: Int,
    val manualRestorations: List<ManualRestoreItem>,
    val timestamp: Long = System.currentTimeMillis()
)

data class ManualRestoreItem(
    val settingName: String,
    val explanation: String,
    val actionIntentName: String? = null
)
