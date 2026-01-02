package com.vkm.healthmonitor.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vkm.healthmonitor.core.model.DailyEnergyScore
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergyScoreDao {
    @Query("SELECT * FROM daily_energy_score ORDER BY date DESC LIMIT 1")
    fun getLatestScore(): Flow<DailyEnergyScore?>

    @Query("SELECT * FROM daily_energy_score WHERE date = :date")
    suspend fun getScoreForDate(date: String): DailyEnergyScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: DailyEnergyScore)
    
    @Query("SELECT * FROM daily_energy_score ORDER BY date DESC LIMIT 30")
    fun getHistory(): Flow<List<DailyEnergyScore>>
}
