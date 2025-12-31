package com.vkm.healthmonitor.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.core.data.repository.HealthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


@HiltWorker
class StandardsRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: HealthRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        repo.refreshStandardsFromFirebase()
        repo.refreshPlansFromFirebase()
        return Result.success()
    }
}

