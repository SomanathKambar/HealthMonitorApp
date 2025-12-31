package com.vkm.healthmonitor.core.data.worker


import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object HydrationSyncManager {

    private const val UNIQUE_WORK_NAME = "hydration_sync_worker"

    fun schedulePeriodicSync(context: Context) {
        val request = PeriodicWorkRequestBuilder<HydrationSyncWorker>(
            6, TimeUnit.HOURS // Change frequency as needed
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

    }

    fun scheduleOneTimeSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<HydrationSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
