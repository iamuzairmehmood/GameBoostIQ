package com.iamuzairmehmood.gameboostiq.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameBoostRepository(
    private val dao: GameBoostDao,
    private val context: Context
) {
    val gameProfiles: Flow<List<GameProfileEntity>> = dao.getAllGameProfiles()
    val allSessions: Flow<List<GamingSessionEntity>> = dao.getAllSessions()

    suspend fun insertGameProfile(profile: GameProfileEntity) {
        withContext(Dispatchers.IO) {
            dao.insertGameProfile(profile)
        }
    }

    suspend fun updateGameProfile(profile: GameProfileEntity) {
        withContext(Dispatchers.IO) {
            dao.updateGameProfile(profile)
        }
    }

    suspend fun deleteGameProfile(packageName: String) {
        withContext(Dispatchers.IO) {
            dao.deleteGameProfile(packageName)
        }
    }

    suspend fun recordSession(session: GamingSessionEntity): Long {
        return withContext(Dispatchers.IO) {
            dao.insertSession(session)
        }
    }

    suspend fun clearSessionHistory() {
        withContext(Dispatchers.IO) {
            dao.clearAllSessions()
        }
    }

    suspend fun scanAndSeedInstalledGames() {
        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolvedApps = pm.queryIntentActivities(mainIntent, 0)
            val foundGames = mutableListOf<GameProfileEntity>()

            // Known popular gaming package signatures 
            val popularGameKeywords = listOf(
                "freefire", "free fire", "garena", "pubg", "cod", "genshin", "roblox",
                "minecraft", "mobilelegends", "asphalt", "clash", "brawl",
                "subway", "candy", "fifa", "fortnite", "honorofkings"
            )

            for (resolveInfo in resolvedApps) {
                val appInfo = resolveInfo.activityInfo.applicationInfo
                val pkg = appInfo.packageName
                if (pkg == context.packageName) continue

                val isGameCategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appInfo.category == ApplicationInfo.CATEGORY_GAME
                } else false

                val isKnownGame = popularGameKeywords.any { kw ->
                    pkg.lowercase().contains(kw) || resolveInfo.loadLabel(pm).toString().lowercase().contains(kw)
                }

                if (isGameCategory || isKnownGame) {
                    val appName = resolveInfo.loadLabel(pm).toString()
                    foundGames.add(
                        GameProfileEntity(
                            packageName = pkg,
                            appName = appName,
                            isCustomAdded = false,
                            performanceMode = "PERFORMANCE",
                            fpsOverlayEnabled = true,
                            tempOverlayEnabled = true,
                            pingOverlayEnabled = true
                        )
                    )
                }
            }

            // Always provide high-profile game presets for detected games
            if (foundGames.none { it.packageName.contains("freefire") }) {
                foundGames.add(
                    0,
                    GameProfileEntity(
                        packageName = "com.dts.freefireth",
                        appName = "Game Title",
                        isCustomAdded = true,
                        performanceMode = "PERFORMANCE"
                    )
                )
                foundGames.add(
                    1,
                    GameProfileEntity(
                        packageName = "com.dts.freefiremax",
                        appName = "Another Game",
                        isCustomAdded = true,
                        performanceMode = "PERFORMANCE"
                    )
                )
            }

            dao.insertGameProfiles(foundGames)
        }
    }
}
