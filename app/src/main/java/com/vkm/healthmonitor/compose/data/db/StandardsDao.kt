package com.vkm.healthmonitor.compose.data.db

import androidx.room.*
import com.vkm.healthmonitor.compose.data.model.HealthStandard
import kotlinx.coroutines.flow.Flow

@Dao
interface StandardsDao {
    @Query("SELECT * FROM standards")
    fun all(): Flow<List<HealthStandard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(s: HealthStandard)

    @Query("SELECT value FROM standards WHERE healthKey = :healthKey LIMIT 1")
    suspend fun getValue(healthKey: String): String?
}

