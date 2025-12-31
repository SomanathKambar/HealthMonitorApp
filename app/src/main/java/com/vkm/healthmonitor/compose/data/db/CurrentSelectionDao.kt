package com.vkm.healthmonitor.compose.data.db


import androidx.room.*
import com.vkm.healthmonitor.core.model.CurrentSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentSelectionDao {
    @Query("SELECT profileId FROM current_selection LIMIT 1")
    fun currentProfileIdFlow(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(sel: CurrentSelection)
}
