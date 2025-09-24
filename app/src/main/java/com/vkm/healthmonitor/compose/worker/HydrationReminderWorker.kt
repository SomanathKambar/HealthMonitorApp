package com.vkm.healthmonitor.compose.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.compose.util.NotificationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HydrationReminderWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val profileId = inputData.getInt("profileId", 0)
        NotificationUtil.showHydrationReminder(ctx, profileId)
        Result.success()
    }
}
