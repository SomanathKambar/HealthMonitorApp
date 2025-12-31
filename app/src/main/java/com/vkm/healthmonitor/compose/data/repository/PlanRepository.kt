package com.vkm.healthmonitor.compose.data.repository

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.compose.data.db.AppDatabase
import com.vkm.healthmonitor.core.model.HealthPlan
import com.vkm.healthmonitor.core.common.awaitTask
import com.vkm.healthmonitor.compose.worker.SchedulePlanNotificationWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class PlanRepository @Inject constructor(
    private val db: AppDatabase,
    private val fs: FirebaseFirestore,
    @ApplicationContext private val ctx: Context
) {
    private val plansDao = db.plansDao()

    fun plansForProfileFlow(profileId: Int) = plansDao.plansForProfile(profileId)

    suspend fun insertPlan(plan: HealthPlan) = withContext(Dispatchers.IO) {
        val id = plansDao.insert(plan)
        schedulePlanNotification(plan.copy(id = id))
        // push to server (best-effort)
        try {
            val map = mapOf("profileId" to plan.profileId, "title" to plan.title, "description" to plan.description, "repeatHours" to plan.repeatHours)
            fs.collection("plans").add(map).awaitTask()
        } catch (_: Exception) {}
        id
    }

    suspend fun deletePlan(planId: Long) = withContext(Dispatchers.IO) {
        plansDao.deleteById(planId)
        // server cleanup optional
    }

    private fun schedulePlanNotification(plan: HealthPlan) {
        val req = PeriodicWorkRequestBuilder<SchedulePlanNotificationWorker>(plan.repeatHours.toLong(), TimeUnit.HOURS)
            .setInputData(workDataOf("planId" to plan.id.toInt(), "title" to plan.title, "desc" to plan.description))
            .build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("plan_${plan.id}", ExistingPeriodicWorkPolicy.REPLACE, req)
    }

    // one-time server -> local refresh of template plans
    suspend fun refreshPlansFromServer() = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("config").document("health_plans").get().awaitTask()
            val data = snap.data ?: return@withContext
            // expected structure: map of key -> "title::desc::hours"
            data.forEach { (_, v) ->
                val parts = v.toString().split("::")
                if (parts.size >= 2) {
                    val title = parts[0]; val desc = parts[1]; val hrs = parts.getOrNull(2)?.toIntOrNull() ?: 24
                    plansDao.insert(HealthPlan(profileId = 0, title = title, description = desc, repeatHours = hrs, active = true, source = "server"))
                }
            }
        } catch (_: Exception) {}
    }
}
