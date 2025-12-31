package com.vkm.healthmonitor.compose.data.db

import androidx.room.*
import com.vkm.healthmonitor.core.model.VitalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    @Query("SELECT * FROM vitals WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun allForProfile(profileId: Int): Flow<List<VitalEntry>>

    @Query("SELECT * FROM vitals WHERE profileId = :profileId AND date = :date ORDER BY timestamp DESC")
    fun forProfileByDate(profileId: Int, date: String): Flow<List<VitalEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vital: VitalEntry): Long

    @Delete
    suspend fun delete(vital: VitalEntry)

    @Query("SELECT * FROM vitals WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun getVitalsForProfileFlow(profileId: Int): Flow<List<VitalEntry>>

    @Query("SELECT * FROM vitals WHERE profileId = :profileId ORDER BY timestamp DESC")
    suspend fun getVitalsForProfileList(profileId: Int): List<VitalEntry>
}
