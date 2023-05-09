package com.example.vktest.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FileHashEntity::class], version = 1)
abstract class FileHashDatabase : RoomDatabase() {

    abstract fun fileHashDao(): FileHashDao

    companion object {
        private const val DB_NAME = "files-hash-database"
        @Volatile private var instance: FileHashDatabase? = null

        fun getInstance(context: Context): FileHashDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): FileHashDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FileHashDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}