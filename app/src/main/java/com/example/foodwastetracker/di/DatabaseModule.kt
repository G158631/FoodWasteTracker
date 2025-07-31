package com.example.foodwastetracker.di

import android.content.Context
import androidx.room.Room
import com.example.foodwastetracker.data.database.AppDatabase
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.database.dao.WasteLogDao
import com.example.foodwastetracker.data.database.dao.UserSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideFoodItemDao(database: AppDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    fun provideWasteLogDao(database: AppDatabase): WasteLogDao {
        return database.wasteLogDao()
    }

    @Provides
    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }
}