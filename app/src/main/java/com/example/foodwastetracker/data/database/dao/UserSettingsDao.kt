package com.example.foodwastetracker.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.foodwastetracker.data.database.entities.UserSettings

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(userSettings: UserSettings)

    @Update
    suspend fun updateUserSettings(userSettings: UserSettings)

    @Query("UPDATE user_settings SET notificationsEnabled = :enabled WHERE id = 1")
    suspend fun updateNotificationSettings(enabled: Boolean)

    @Query("UPDATE user_settings SET expirationWarningDays = :days WHERE id = 1")
    suspend fun updateWarningDays(days: Int)
}