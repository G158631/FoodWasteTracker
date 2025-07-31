package com.example.foodwastetracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val purchaseDate: Long = System.currentTimeMillis(),
    val expirationDate: Long,
    val quantity: Int = 1,
    val unit: String = "pieces",
    val isConsumed: Boolean = false,
    val dateConsumed: Long? = null,
    val photoPath: String? = null
)