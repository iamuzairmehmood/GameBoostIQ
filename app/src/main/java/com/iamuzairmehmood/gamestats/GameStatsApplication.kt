package com.iamuzairmehmood.gamestats

import android.app.Application
import com.iamuzairmehmood.gamestats.data.GameStatsDatabase
import com.iamuzairmehmood.gamestats.data.GameStatsRepository
import com.iamuzairmehmood.gamestats.manager.DeviceCapabilityScanner
import com.iamuzairmehmood.gamestats.manager.GamingModeManager
import com.iamuzairmehmood.gamestats.monitor.PerformanceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameStatsApplication : Application() {

    lateinit var database: GameStatsDatabase
        private set

    lateinit var repository: GameStatsRepository
        private set

    lateinit var performanceMonitor: PerformanceMonitor
        private set

    lateinit var capabilityScanner: DeviceCapabilityScanner
        private set

    lateinit var gamingModeManager: GamingModeManager
        private set

    override fun onCreate() {
        super.onCreate()

        database = GameStatsDatabase.getInstance(this)
        repository = GameStatsRepository(database.gameBoostDao(), this)
        performanceMonitor = PerformanceMonitor(this)
        capabilityScanner = DeviceCapabilityScanner(this)
        gamingModeManager = GamingModeManager(this, performanceMonitor, repository)

        // Seed games asynchronously on initial launch
        CoroutineScope(Dispatchers.IO).launch {
            repository.scanAndSeedInstalledGames()
        }
    }
}
