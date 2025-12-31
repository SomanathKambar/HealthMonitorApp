package com.vkm.healthmonitor.compose.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.compose.data.db.AppDatabase
import com.vkm.healthmonitor.core.model.HydrationLog
import com.vkm.healthmonitor.core.model.HydrationResult
import com.vkm.healthmonitor.compose.data.validator.HydrationLogic
import com.vkm.healthmonitor.compose.prefrence.HydrationPrefs
import com.vkm.healthmonitor.compose.util.HydrationUtil
import com.vkm.healthmonitor.core.common.awaitTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject


class HydrationRepository @Inject constructor(
    private val db: AppDatabase,
    private val fs: FirebaseFirestore,
    @ApplicationContext private val ctx: Context
) {
    private val hydrationDao = db.hydrationDao()
    private val profileDao = db.profileDao()
    val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            fetchAllProfiles()
        }
    }

//    suspend fun insertHydration(profileId: Int, amountMl: Int) = withContext(Dispatchers.IO) {
//        val log = HydrationLog(profileId = profileId, amountMl = amountMl)
//        hydrationDao.insert(log)
//        HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())
//        scheduleNextReminder(profileId)
//        try {
//            val map = mapOf(
//                "profileId" to profileId,
//                "timestamp" to log.timestamp,
//                "amountMl" to amountMl
//            )
//            fs.collection("hydration").add(map).awaitTask()
//        } catch (_: Exception) {
//        }
//    }

    suspend fun insertHydration(profileId: Int, amountMl: Int) = withContext(Dispatchers.IO) {
        val log = HydrationLog(profileId = profileId, amountMl = amountMl)
        hydrationDao.insert(log)
        HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())

        // Get profile and today's hydration total
        val profile = db.profileDao().getById(profileId) ?: return@withContext
        val (start, end) = HydrationUtil.getDayBounds(System.currentTimeMillis())
        val totalToday = hydrationDao.hydrationBetween(profileId, start, end).firstOrNull()?.sumOf { it.amountMl }?: 0
        val goal = HydrationLogic.computeDailyGoal(profile)
        val max = HydrationLogic.maxSafeIntake(goal)

        val intervalMinutes = HydrationUtil.computeNextReminderIntervalMinutes(
            lastIntakeTime = log.timestamp,
            currentTotalMl = totalToday,
            goalMl = goal,
            safeMaxMl = max
        )

        if (intervalMinutes > 0) {
            HydrationUtil.scheduleNextReminder(ctx, profileId, intervalMinutes)
        }

        // Save to Firestore
        try {
            val map = mapOf(
                "profileId" to profileId,
                "timestamp" to log.timestamp,
                "amountMl" to amountMl
            )
            fs.collection("hydration").add(map).awaitTask()
        } catch (_: Exception) {
            // ignore Firestore failure
        }
    }


//    private fun scheduleNextReminder(profileId: Int, intervalHours: Long = 2L) {
//        val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
//            .setInitialDelay(intervalHours, TimeUnit.HOURS)
//            .setInputData(workDataOf(AppConstants.WORK_DATA_KEY_PROFILE_ID to profileId))
//            .build()
//        WorkManager.getInstance(ctx)
//            .enqueueUniqueWork("hydration_for_$profileId", ExistingWorkPolicy.REPLACE, req)
//    }

//    suspend fun removeLastHydration(profileId: Int) = withContext(Dispatchers.IO) {
//        val last = hydrationDao.lastForProfile(profileId)
//        last?.let { hydrationDao.deleteById(it.id) }
//    }

    // sum for today
    fun todayHydrationFlowForProfile(profileId: Int): Flow<Int> = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(
        Calendar.SECOND,
        0
    ); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis
        hydrationDao.hydrationBetween(profileId, start, end)
            .map { list -> list.sumOf { it.amountMl } }
    }


    suspend fun fetchAllProfiles() {
        profileDao.getAllProfiles().firstOrNull()?.let { profiles ->
            profiles.forEach { profile ->
                refreshHydrationFromServer(profile.id)
            }
        }
    }

    // optional server -> local sync
    suspend fun refreshHydrationFromServer(profileId: Int) = withContext(Dispatchers.IO) {
        try {
            val snap =
                fs.collection("hydration").whereEqualTo("profileId", profileId).get().awaitTask()
            snap.documents.forEach { doc ->
                val data = doc.data ?: return@forEach
                val h = HydrationLog(
                    profileId = (data["profileId"] as? Long)?.toInt() ?: profileId,
                    timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis(),
                    amountMl = (data["amountMl"] as? Long)?.toInt() ?: 0
                )
                hydrationDao.insert(h)
            }
        } catch (_: Exception) {
        }
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
        return hydrationDao.getHydrationForProfile(profileId)
    }

    suspend fun removeById(id: Int) = hydrationDao.deleteById(id)
    suspend fun lastForProfile(profileId: Int): HydrationLog? =
        hydrationDao.lastForProfile(profileId)

    // Get today's total for a profile as Flow<Int>
//    fun todayTotalFlow(profileId: Int): Flow<Int> {
//        val cal = Calendar.getInstance()
//        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
//        val start = cal.timeInMillis
//        val end = start + 24*60*60*1000 - 1
//        return hydrationDao.hydrationBetween(profileId, start, end).map { list -> list.sumOf { it.amountMl } }
//    }

    // helper to get logs list between
//    fun todayLogsFlow(profileId: Int) = run {
//        val cal = Calendar.getInstance()
//        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
//        val start = cal.timeInMillis
//        val end = start + 24*60*60*1000 - 1
//        hydrationDao.hydrationBetween(profileId, start, end)
//    }


//    suspend fun tryAddHydration(profileId: Int, amountMl: Int): HydrationResult {
//        val profile =
//            profileDao.getById(profileId) ?: return HydrationResult.Error("Profile not found")
//        val goal = HydrationLogic.computeDailyGoal(profile)
//        val safeMax = HydrationLogic.maxSafeIntake(goal)
//
//        // get today's total
//        val (start, end) = todayBounds()
//        val current = hydrationDao.hydrationBetween(profileId, start, end).firstOrNull()
//            ?.sumOf { it.amountMl } ?: 0
//
//        if (current + amountMl > safeMax) {
//            return HydrationResult.ExceedsSafe(safeMax)
//        }
//
//        // else insert
//        val log = HydrationLog(profileId = profileId, amountMl = amountMl)
//        hydrationDao.insert(log)
//        // (also update last reminder and schedule next etc.)
//        // ...
//        return HydrationResult.Success
//    }

    suspend fun tryAddHydration(profileId: Int, amountMl: Int): HydrationResult = withContext(Dispatchers.IO) {
        val profile = profileDao.getById(profileId)
            ?: return@withContext HydrationResult.Error("Profile not found")

        val goal = HydrationLogic.computeDailyGoal(profile)
        val safeMax = HydrationLogic.maxSafeIntake(goal)

        val (start, end) = HydrationUtil.getDayBounds(System.currentTimeMillis())
        val totalToday = hydrationDao.hydrationBetween(profileId, start, end).firstOrNull()?.sumOf { it.amountMl } ?: 0

        if (totalToday + amountMl > safeMax) {
            return@withContext HydrationResult.ExceedsSafe(safeMax)
        }

        // ✅ Safe to insert
        val log = HydrationLog(profileId = profileId, amountMl = amountMl)
        hydrationDao.insert(log)

        // ✅ Update last reminder timestamp
        HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())

        // ✅ Compute and schedule next reminder
        val intervalMinutes = HydrationUtil.computeNextReminderIntervalMinutes(
            lastIntakeTime = log.timestamp,
            currentTotalMl = totalToday + amountMl,
            goalMl = goal,
            safeMaxMl = safeMax
        )

        if (intervalMinutes > 0) {
            HydrationUtil.scheduleNextReminder(ctx, profileId, intervalMinutes)
        }

        // ✅ Sync to Firestore
        try {
            val map = mapOf(
                "profileId" to profileId,
                "timestamp" to log.timestamp,
                "amountMl" to amountMl
            )
            val docRef = fs.collection("hydration").add(map).awaitTask()
            hydrationDao.updateFirestoreId(log.id, docRef.id)
        } catch (_: Exception) {
            // ignore Firestore failures silently
        }

        return@withContext HydrationResult.Success
    }

    suspend fun removeLastHydration(profileId: Int) = withContext(Dispatchers.IO) {
        val last = hydrationDao.lastForProfile(profileId)
        if (last != null) {
            hydrationDao.deleteById(last.id)

            val fsId = last.firestoreId
            if (fsId != null) {
                try {
                    fs.collection("hydration").document(fsId).delete().awaitTask()
                } catch (e: Exception) {
                    e.printStackTrace() // handle sync issue
                }
            }
        }
    }

    suspend fun syncUnsyncedLogs() = withContext(Dispatchers.IO) {
        val unsyncedLogs = hydrationDao.getLogsWhereFirestoreIdIsNull()
        for (log in unsyncedLogs) {
            try {
                val map = mapOf(
                    "profileId" to log.profileId,
                    "timestamp" to log.timestamp,
                    "amountMl" to log.amountMl
                )
                val docRef = fs.collection("hydration").add(map).awaitTask()
                hydrationDao.updateFirestoreId(log.id, docRef.id)
            } catch (e: Exception) {
                // Still offline? Try again later
            }
        }
    }



    fun todayTotalFlow(profileId: Int): Flow<Int> {
        val (start, end) = todayBounds()
        return hydrationDao.hydrationBetween(profileId, start, end)
            .map { list -> list.sumOf { it.amountMl } }
    }

    fun todayLogsFlow(profileId: Int): Flow<List<HydrationLog>> {
        val (start, end) = todayBounds()
        return hydrationDao.hydrationBetween(profileId, start, end)
    }

    private fun todayBounds(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }
}
