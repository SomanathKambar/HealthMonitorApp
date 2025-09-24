package com.vkm.healthmonitor.compose.worker


import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.compose.data.repository.HealthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SchedulePlanNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: HealthRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val planId = inputData.getLong("planId", 0L)
        // find plan in DB, notify
        // For simplicity, NotificationUtil can accept plan text and show notification
        // Implementation left to repository retrieval or pass plan data in inputData
        return Result.success()
    }
}
