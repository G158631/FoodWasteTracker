package com.example.foodwastetracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "waste_logs")
data class WasteLog(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val foodItemId: String,
    val foodName: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val reason: String, // expired, spoiled, too_much, etc.
    val dateWasted: Long,
    val estimatedValue: Double? = null
)