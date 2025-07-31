package com.example.foodwastetracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "18:00",
    val expirationWarningDays: Int = 3
)