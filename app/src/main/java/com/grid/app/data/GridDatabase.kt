package com.grid.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Topic::class, TodoItem::class, LogEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GridDatabase : RoomDatabase() {
    abstract fun dao(): GridDao

    companion object {
        @Volatile private var INSTANCE: GridDatabase? = null

        fun getInstance(context: Context): GridDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GridDatabase::class.java,
                    "grid_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
