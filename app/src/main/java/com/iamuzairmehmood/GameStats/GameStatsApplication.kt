package com.iamuzairmehmood.GameStats

import android.app.Application
import com.iamuzairmehmood.GameStats.data.GameStatsDatabase
import com.iamuzairmehmood.GameStats.data.GameStatsRepository
import com.iamuzairmehmood.GameStats.manager.DeviceCapabilityScanner
import com.iamuzairmehmood.GameStats.manager.GamingModeManager
import com.iamuzairmehmood.GameStats.monitor.PerformanceMonitor
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
