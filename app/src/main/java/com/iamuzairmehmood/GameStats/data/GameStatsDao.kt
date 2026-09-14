package com.iamuzairmehmood.GameStats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameStatsDao {

    // Game Profiles
    @Query("SELECT * FROM game_profiles ORDER BY lastPlayedTimestamp DESC, appName ASC")
    fun getAllGameProfiles(): Flow<List<GameProfileEntity>>

    @Query("SELECT * FROM game_profiles WHERE packageName = :packageName LIMIT 1")
    suspend fun getGameProfile(packageName: String): GameProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameProfile(profile: GameProfileEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGameProfiles(profiles: List<GameProfileEntity>)

    @Update
    suspend fun updateGameProfile(profile: GameProfileEntity)

    @Query("DELETE FROM game_profiles WHERE packageName = :packageName")
    suspend fun deleteGameProfile(packageName: String)

    // Gaming Sessions
    @Query("SELECT * FROM gaming_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<GamingSessionEntity>>

    @Query("SELECT * FROM gaming_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<GamingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GamingSessionEntity): Long

    @Query("DELETE FROM gaming_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Query("DELETE FROM gaming_sessions")
    suspend fun clearAllSessions()
}
