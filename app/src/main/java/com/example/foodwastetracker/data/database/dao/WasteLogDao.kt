package com.example.foodwastetracker.data.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.foodwastetracker.data.database.entities.WasteLog

@Dao
interface WasteLogDao {
    @Query("SELECT * FROM waste_logs ORDER BY dateWasted DESC")
    fun getAllWasteLogs(): Flow<List<WasteLog>>

    @Query("SELECT * FROM waste_logs WHERE dateWasted >= :startDate AND dateWasted <= :endDate")
    fun getWasteLogsByDateRange(startDate: Long, endDate: Long): Flow<List<WasteLog>>

    @Insert
    suspend fun insertWasteLog(wasteLog: WasteLog)

    @Query("SELECT SUM(quantity) FROM waste_logs WHERE dateWasted >= :startDate")
    fun getTotalWasteFromDate(startDate: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM waste_logs WHERE dateWasted >= :startDate")
    fun getWasteCountFromDate(startDate: Long): Flow<Int>
}