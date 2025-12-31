package com.vkm.healthmonitor.core.database

import androidx.room.*
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.model.ProfileWithVitals
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun allProfiles(): Flow<List<Profile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile): Long

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)

    @Query("SELECT * FROM profiles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Profile?

    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun getAllProfiles(): Flow<List<Profile>>

    @Transaction
    @Query("SELECT * FROM profiles ORDER BY id ASC")
    fun getAllWithVitals(): Flow<List<ProfileWithVitals>>
}
