package com.vkm.healthmonitor.core.data.repository


import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.data.worker.HydrationReminderWorker
import com.vkm.healthmonitor.core.data.worker.SchedulePlanNotificationWorker
import com.vkm.healthmonitor.core.common.constant.AppConstants
import com.vkm.healthmonitor.core.common.awaitTask
import com.vkm.healthmonitor.core.model.CurrentSelection
import com.vkm.healthmonitor.core.model.HealthPlan
import com.vkm.healthmonitor.core.model.HealthStandard
import com.vkm.healthmonitor.core.model.HydrationLog
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.model.VitalEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HealthRepository @Inject constructor(
    private val db: AppDatabase, 
    private val fs: FirebaseFirestore, 
    private val workManager: WorkManager,
    @ApplicationContext private val ctx: Context
) {

    private val profileDao = db.profileDao()
    private val vitalsDao = db.vitalsDao()
    private val hydrationDao = db.hydrationDao()
    private val standardsDao = db.standardsDao()
    private val plansDao = db.plansDao()
    private val selDao = db.selectionDao()

    // flows
    fun allProfilesFlow(): Flow<List<Profile>> = profileDao.allProfiles()
    fun currentProfileIdFlow(): Flow<Int?> = selDao.currentProfileIdFlow()

    fun vitalsFor(profileId: Int): Flow<List<VitalEntry>> = vitalsDao.allForProfile(profileId)
    fun hydrationBetween(profileId: Int, from: Long, to: Long) = hydrationDao.hydrationBetween(profileId, from, to)
    fun plansFor(profileId: Int): Flow<List<HealthPlan>> = plansDao.plansForProfile(profileId)
    fun standardsFlow() = standardsDao.all()

    // insert/profile management
    suspend fun insertProfile(p: Profile): Long = withContext(Dispatchers.IO) {
        val bmi = Profile.computeBmi(p.heightCm, p.weightKg)
        val toInsert = p.copy(bmi = bmi)
        val id = profileDao.insert(toInsert).also { newId ->
            // upload best-effort
            try { uploadProfileToFirestore(toInsert.copy(id = newId.toInt())) } catch (_: Exception) {}
        }
        id
    }

    suspend fun setCurrentProfile(profileId: Int) = withContext(Dispatchers.IO) {
        selDao.upsert(CurrentSelection(profileId = profileId))
    }

    suspend fun deleteProfile(p: Profile) = withContext(Dispatchers.IO) {
        profileDao.delete(p)
        // optionally delete related records; left to implement policy
    }

    suspend fun insertVital(profileId: Int, entry: VitalEntry) = withContext(Dispatchers.IO) {
        val withProfile = entry.copy(profileId = profileId)
        vitalsDao.insert(withProfile)
        try { uploadVitalToFirestore(withProfile) } catch (_: Exception) {}
    }

    suspend fun insertPlan(plan: HealthPlan) = withContext(Dispatchers.IO) {
        val id = plansDao.insert(plan)
        // schedule plan notifications via WorkManager
        schedulePlanNotification(plan.copy(id = id))
        id
    }

    suspend fun refreshStandardsFromFirebase() = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("config").document("health_standards").get().awaitTask()
            snap.data?.forEach { (k, v) ->
                standardsDao.upsert(HealthStandard(k, v.toString()))
            }
        } catch (_: Exception) {}
    }

    suspend fun refreshPlansFromFirebase() = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("config").document("health_plans").get().awaitTask()
            val map = snap.data
            map?.forEach { (k, v) ->
                // expected v to be a list/JSON; for simplicity, we'll parse as text entries "key::title::desc::hours"
                val parts = v.toString().split("::")
                if (parts.size >= 3) {
                    val title = parts[0]; val desc = parts[1]; val hrs = parts.getOrNull(2)?.toIntOrNull() ?: 24
                    // Upsert into local DB as source=server and profileId=0 (global templates)
                    plansDao.insert(HealthPlan(profileId = 0, title = title, description = desc, repeatHours = hrs, active = true, source = "server"))
                }
            }
        } catch (_: Exception) {}
    }

    // helper: schedule next hydration reminder (reads standards hydration interval or default 2 hours)
    fun scheduleNextHydration(profileId: Int) {
        val intervalHours = 2L // could read from standards via coroutine if needed
        HydrationReminderWorker.scheduleNextReminder(ctx, profileId, intervalHours, TimeUnit.HOURS)
    }

    fun schedulePlanNotification(plan: HealthPlan) {
        val req = PeriodicWorkRequestBuilder<SchedulePlanNotificationWorker>(plan.repeatHours.toLong(), TimeUnit.HOURS)
            .setInputData(workDataOf("planId" to plan.id))
            .build()
        workManager.enqueueUniquePeriodicWork("plan_${plan.id}", ExistingPeriodicWorkPolicy.REPLACE, req)
    }

    // upload helpers (best-effort)
    private suspend fun uploadProfileToFirestore(p: Profile) {
        val map = mapOf("name" to p.name, "age" to p.age, "gender" to p.gender, "heightCm" to p.heightCm, "weightKg" to p.weightKg, "bmi" to p.bmi)
        fs.collection("profiles").document(p.id.toString()).set(map).awaitTask()
    }

    private suspend fun uploadVitalToFirestore(v: VitalEntry) {
        val map = mapOf("profileId" to v.profileId, "timestamp" to v.timestamp, "pulse" to v.pulse, "bpSys" to v.bpSys, "bpDia" to v.bpDia, "temperature" to v.temperature, "spo2" to v.spo2)
        fs.collection("vitals").add(map).awaitTask()
    }

    private suspend fun uploadHydrationToFirestore(h: HydrationLog) {
        val map = mapOf("profileId" to h.profileId, "timestamp" to h.timestamp, "amountMl" to h.amountMl)
        fs.collection("hydration").add(map).awaitTask()
    }

    fun todayHydrationFlowForProfile(profileId: Int) = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis
        hydrationDao.hydrationBetween(profileId, start, end).map { list -> list.sumOf { it.amountMl } }
    }
}