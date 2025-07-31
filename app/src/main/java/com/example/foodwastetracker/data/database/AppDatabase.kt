package com.example.foodwastetracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.example.foodwastetracker.data.database.entities.FoodItem
import com.example.foodwastetracker.data.database.entities.WasteLog
import com.example.foodwastetracker.data.database.entities.UserSettings
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.database.dao.WasteLogDao
import com.example.foodwastetracker.data.database.dao.UserSettingsDao
import java.util.Date

@Database(
    entities = [FoodItem::class, WasteLog::class, UserSettings::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun wasteLogDao(): WasteLogDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        const val DATABASE_NAME = "food_waste_tracker_db"
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}