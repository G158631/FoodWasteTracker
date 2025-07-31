package com.example.foodwastetracker.di

import android.content.Context
import androidx.room.Room
import com.example.foodwastetracker.data.database.AppDatabase
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.repository.FoodRepository

object DatabaseModule {

    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    fun provideFoodItemDao(database: AppDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    fun provideFoodRepository(foodItemDao: FoodItemDao): FoodRepository {
        return FoodRepository(foodItemDao)
    }
}