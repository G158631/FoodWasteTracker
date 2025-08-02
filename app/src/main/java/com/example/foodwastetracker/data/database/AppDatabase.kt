package com.example.foodwastetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.database.entities.FoodItem

@Database(
    entities = [FoodItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao

    companion object {
        const val DATABASE_NAME = "food_waste_tracker_db"
    }
}