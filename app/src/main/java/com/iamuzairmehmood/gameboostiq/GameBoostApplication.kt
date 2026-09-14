package com.iamuzairmehmood.gameboostiq

import android.app.Application
import com.iamuzairmehmood.gameboostiq.data.GameBoostDatabase
import com.iamuzairmehmood.gameboostiq.data.GameBoostRepository
import com.iamuzairmehmood.gameboostiq.manager.DeviceCapabilityScanner
import com.iamuzairmehmood.gameboostiq.manager.GamingModeManager
import com.iamuzairmehmood.gameboostiq.monitor.PerformanceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameBoostApplication : Application() {

    lateinit var database: GameBoostDatabase
        private set

    lateinit var repository: GameBoostRepository
        private set

    lateinit var performanceMonitor: PerformanceMonitor
        private set

    lateinit var capabilityScanner: DeviceCapabilityScanner
        private set

    lateinit var gamingModeManager: GamingModeManager
        private set

    override fun onCreate() {
        super.onCreate()

        database = GameBoostDatabase.getInstance(this)
        repository = GameBoostRepository(database.gameBoostDao(), this)
        performanceMonitor = PerformanceMonitor(this)
        capabilityScanner = DeviceCapabilityScanner(this)
        gamingModeManager = GamingModeManager(this, performanceMonitor, repository)

        // Seed games asynchronously on initial launch
        CoroutineScope(Dispatchers.IO).launch {
            repository.scanAndSeedInstalledGames()
        }
    }
}
