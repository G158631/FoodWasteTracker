package com.example.foodwastetracker.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.database.dao.WasteLogDao
import com.example.foodwastetracker.data.database.dao.UserSettingsDao
import com.example.foodwastetracker.data.database.entities.FoodItem
import com.example.foodwastetracker.data.database.entities.WasteLog
import com.example.foodwastetracker.data.database.entities.UserSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val wasteLogDao: WasteLogDao,
    private val userSettingsDao: UserSettingsDao
) {
    // Food Items
    fun getAllFoodItems(): Flow<List<FoodItem>> = foodItemDao.getAllActiveFoodItems()

    fun getExpiringItems(warningDays: Int): Flow<List<FoodItem>> {
        val warningDate = System.currentTimeMillis() + (warningDays * 24 * 60 * 60 * 1000)
        return foodItemDao.getExpiringItems(warningDate)
    }

    suspend fun addFoodItem(foodItem: FoodItem) {
        foodItemDao.insertFoodItem(foodItem)
    }

    suspend fun updateFoodItem(foodItem: FoodItem) {
        foodItemDao.updateFoodItem(foodItem)
    }

    suspend fun markAsConsumed(foodItem: FoodItem) {
        foodItemDao.markAsConsumed(foodItem.id, System.currentTimeMillis())
    }

    suspend fun deleteFoodItem(foodItem: FoodItem) {
        foodItemDao.deleteFoodItem(foodItem)
    }

    // Waste Logging
    suspend fun logWaste(foodItem: FoodItem, reason: String, estimatedValue: Double? = null) {
        val wasteLog = WasteLog(
            foodItemId = foodItem.id,
            foodName = foodItem.name,
            category = foodItem.category,
            quantity = foodItem.quantity,
            unit = foodItem.unit,
            reason = reason,
            dateWasted = System.currentTimeMillis(),
            estimatedValue = estimatedValue
        )
        wasteLogDao.insertWasteLog(wasteLog)
        foodItemDao.deleteFoodItem(foodItem)
    }

    fun getAllWasteLogs(): Flow<List<WasteLog>> = wasteLogDao.getAllWasteLogs()

    fun getWasteLogsByDateRange(startDate: Long, endDate: Long): Flow<List<WasteLog>> =
        wasteLogDao.getWasteLogsByDateRange(startDate, endDate)

    // User Settings
    fun getUserSettings(): Flow<UserSettings?> = userSettingsDao.getUserSettings()

    suspend fun updateUserSettings(userSettings: UserSettings) {
        userSettingsDao.insertUserSettings(userSettings)
    }
}