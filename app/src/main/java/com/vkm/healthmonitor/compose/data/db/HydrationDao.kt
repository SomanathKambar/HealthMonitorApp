package com.vkm.healthmonitor.compose.data.db

import androidx.room.*
import com.vkm.healthmonitor.compose.data.model.HydrationLog
import kotlinx.coroutines.flow.Flow


@Dao
interface HydrationDao {

    @Query("SELECT * FROM hydration WHERE profileId = :profileId AND timestamp BETWEEN :from AND :to ORDER BY timestamp DESC")
    fun hydrationBetween(profileId: Int, from: Long, to: Long): Flow<List<HydrationLog>>

    @Query("SELECT * FROM hydration WHERE profileId = :profileId ORDER BY timestamp DESC LIMIT 1")
    suspend fun lastForProfile2(profileId: Int): HydrationLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HydrationLog): Long

    @Query("DELETE FROM hydration WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM hydration WHERE profileId = :profileId ORDER BY timestamp DESC")
    suspend fun getHydrationForProfile(profileId: Int): List<HydrationLog>

    @Query("SELECT * FROM hydration WHERE profileId = :profileId ORDER BY timestamp DESC LIMIT 1")
    suspend fun lastForProfile(profileId: Int): HydrationLog?

    @Query("SELECT * FROM hydration WHERE profileId = :profileId AND timestamp > :afterTime ORDER BY timestamp DESC Limit 1")
    suspend fun getIntakeAfter(profileId: Int, afterTime: Long): HydrationLog

    @Query("UPDATE hydration SET firestoreId = :firestoreId WHERE id = :localId")
    suspend fun updateFirestoreId(localId: Int, firestoreId: String)

    @Query("SELECT * FROM hydration WHERE firestoreId IS NULL")
    suspend fun getLogsWhereFirestoreIdIsNull(): List<HydrationLog>

}

