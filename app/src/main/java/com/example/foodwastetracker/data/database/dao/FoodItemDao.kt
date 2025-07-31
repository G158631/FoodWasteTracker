package com.example.foodwastetracker.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.foodwastetracker.data.database.entities.FoodItem

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE isConsumed = 0 ORDER BY expirationDate ASC")
    fun getAllActiveFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expirationDate <= :warningDate AND isConsumed = 0")
    fun getExpiringItems(warningDate: Long): Flow<List<FoodItem>>

    @Insert
    suspend fun insertFoodItem(foodItem: FoodItem)

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("UPDATE food_items SET isConsumed = 1, dateConsumed = :dateConsumed WHERE id = :id")
    suspend fun markAsConsumed(id: String, dateConsumed: Long)
}