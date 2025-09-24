package com.vkm.healthmonitor.compose.data.db

import androidx.room.*
import com.vkm.healthmonitor.compose.data.model.HealthPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthPlanDao {
    @Query("SELECT * FROM plans WHERE profileId = :profileId")
    fun plansForProfile(profileId: Int): Flow<List<HealthPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: HealthPlan): Long

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun deleteById(id: Long)
}
