package com.iamuzairmehmood.gameboostiq.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GameProfileEntity::class, GamingSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameBoostDatabase : RoomDatabase() {
    abstract fun gameBoostDao(): GameBoostDao

    companion object {
        @Volatile
        private var INSTANCE: GameBoostDatabase? = null

        fun getInstance(context: Context): GameBoostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameBoostDatabase::class.java,
                    "gameboost_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
