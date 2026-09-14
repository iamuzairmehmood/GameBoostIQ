package com.iamuzairmehmood.gamestats.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GameProfileEntity::class, GamingSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameStatsDatabase : RoomDatabase() {
    abstract fun gameBoostDao(): GameStatsDao

    companion object {
        @Volatile
        private var INSTANCE: GameStatsDatabase? = null

        fun getInstance(context: Context): GameStatsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameStatsDatabase::class.java,
                    "gamestats_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
