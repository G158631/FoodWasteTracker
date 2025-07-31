package com.example.foodwastetracker.data.repository

import kotlinx.coroutines.flow.Flow
import com.example.foodwastetracker.data.database.dao.FoodItemDao
import com.example.foodwastetracker.data.database.entities.FoodItem

class FoodRepository(
    private val foodItemDao: FoodItemDao
) {
    fun getAllFoodItems(): Flow<List<FoodItem>> = foodItemDao.getAllActiveFoodItems()

    fun getExpiringItems(days: Int): Flow<List<FoodItem>> {
        val warningDate = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)
        return foodItemDao.getExpiringItems(warningDate)
    }

    suspend fun getFoodItemById(id: String): FoodItem? = foodItemDao.getFoodItemById(id)

    suspend fun addFoodItem(foodItem: FoodItem) = foodItemDao.insertFoodItem(foodItem)

    suspend fun updateFoodItem(foodItem: FoodItem) = foodItemDao.updateFoodItem(foodItem)

    suspend fun deleteFoodItem(foodItem: FoodItem) = foodItemDao.deleteFoodItem(foodItem)

    suspend fun markAsConsumed(id: String) = foodItemDao.markAsConsumed(id, System.currentTimeMillis())

    fun getConsumedItemsCount(): Flow<Int> = foodItemDao.getConsumedItemsCount()

    fun getActiveItemsCount(): Flow<Int> = foodItemDao.getActiveItemsCount()
}