package com.istighfar.app40k.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromEntryType(type: EntryType): String = type.name

    @TypeConverter
    fun toEntryType(value: String): EntryType = EntryType.valueOf(value)
}

@Database(entities = [HistoryEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "istighfar_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
