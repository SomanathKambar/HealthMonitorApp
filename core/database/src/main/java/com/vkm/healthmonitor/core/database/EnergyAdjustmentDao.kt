package com.vkm.healthmonitor.core.database

import androidx.room.*
import com.vkm.healthmonitor.core.model.EnergyAdjustment
import kotlinx.coroutines.flow.Flow

@Dao
interface EnergyAdjustmentDao {
    @Query("SELECT * FROM energy_adjustments WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getAdjustmentsSince(since: Long): Flow<List<EnergyAdjustment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(adjustment: EnergyAdjustment)

    @Query("SELECT SUM(change) FROM energy_adjustments WHERE timestamp >= :since")
    suspend fun getTotalChangeSince(since: Long): Int?
}
