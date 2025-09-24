package com.vkm.healthmonitor.compose.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.compose.data.db.AppDatabase
import com.vkm.healthmonitor.compose.data.model.HydrationLog
import com.vkm.healthmonitor.compose.util.awaitTask
import com.vkm.healthmonitor.compose.worker.HydrationReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HydrationRepository @Inject constructor(
    private val db: AppDatabase,
    private val fs: FirebaseFirestore,
    @ApplicationContext private val ctx: Context
) {
    private val hydrationDao = db.hydrationDao()

    suspend fun insertHydration(profileId: Int, amountMl: Int) = withContext(Dispatchers.IO) {
        val log = HydrationLog(profileId = profileId, amountMl = amountMl)
        hydrationDao.insert(log)
        try {
            val map = mapOf("profileId" to profileId, "timestamp" to log.timestamp, "amountMl" to amountMl)
            fs.collection("hydration").add(map).awaitTask()
        } catch (_: Exception) {}
        scheduleNextReminder(profileId)
    }

    suspend fun removeLastHydration(profileId: Int) = withContext(Dispatchers.IO) {
        val last = hydrationDao.lastForProfile(profileId)
        last?.let { hydrationDao.deleteById(it.id) }
    }

    // sum for today
    fun todayHydrationFlowForProfile(profileId: Int): Flow<Int> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis
        hydrationDao.hydrationBetween(profileId, start, end).map { list -> list.sumOf { it.amountMl } }
    }

    private fun scheduleNextReminder(profileId: Int, intervalHours: Long = 2L) {
        val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setInitialDelay(intervalHours, TimeUnit.HOURS)
            .setInputData(androidx.work.Data.Builder().putInt("profileId", profileId).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("hydration_for_$profileId", ExistingWorkPolicy.REPLACE, req)
    }

    // optional server -> local sync
    suspend fun refreshHydrationFromServer(profileId: Int) = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("hydration").whereEqualTo("profileId", profileId).get().awaitTask()
            snap.documents.forEach { doc ->
                val data = doc.data ?: return@forEach
                val h = HydrationLog(
                    profileId = (data["profileId"] as? Long)?.toInt() ?: profileId,
                    timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis(),
                    amountMl = (data["amountMl"] as? Long)?.toInt() ?: 0
                )
                hydrationDao.insert(h)
            }
        } catch (_: Exception) {}
    }

    fun todayTotal(profileId: Int): Flow<Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val from = cal.timeInMillis
        val to = from + 24 * 60 * 60 * 1000 - 1
        return hydrationDao.hydrationBetween(profileId, from, to).map { logs ->
            logs.sumOf { it.amountMl }

        }
    }

   suspend fun getHydrationForProfile(profileId: Int): List<HydrationLog> {
        return hydrationDao.getHydrationForProfile(profileId)}

    suspend fun removeById(id: Int) = hydrationDao.deleteById(id)
    suspend fun lastForProfile(profileId: Int): HydrationLog? = hydrationDao.lastForProfile(profileId)

    // Get today's total for a profile as Flow<Int>
    fun todayTotalFlow(profileId: Int): Flow<Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24*60*60*1000 - 1
        return hydrationDao.hydrationBetween(profileId, start, end).map { list -> list.sumOf { it.amountMl } }
    }

    // helper to get logs list between
    fun todayLogsFlow(profileId: Int) = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24*60*60*1000 - 1
        hydrationDao.hydrationBetween(profileId, start, end)
    }
}
