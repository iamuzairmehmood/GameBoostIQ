package com.iamuzairmehmood.gameboostiq.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.iamuzairmehmood.gameboostiq.GameBoostApplication
import com.iamuzairmehmood.gameboostiq.MainActivity

class GameBoostTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        try {
            val app = application as? GameBoostApplication ?: return
            val gamingManager = app.gamingModeManager
            val isActive = gamingManager.isGamingModeActive.value

            if (isActive) {
                gamingManager.stopGamingMode()
            } else {
                gamingManager.startGamingMode()
            }

            updateTileState()
        } catch (_: Exception) {
            // Graceful fallback for OEM Quick Settings differences
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        try {
            val app = application as? GameBoostApplication
            val isActive = app?.gamingModeManager?.isGamingModeActive?.value == true

            tile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            tile.label = if (isActive) "Gaming Mode: ON" else "Gaming Mode"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = if (isActive) "GameBoost Active" else "Tap to Boost"
            }

            tile.updateTile()
        } catch (_: Exception) {
            // OEM Tile safety
        }
    }
}
