package com.vkm.healthmonitor.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.common.awaitTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HydrationSyncWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted private val params: WorkerParameters,
    private val db: AppDatabase,
    private val fs: FirebaseFirestore
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val hydrationDao = db.hydrationDao()
            val unsynced = hydrationDao.getLogsWhereFirestoreIdIsNull()
            for (log in unsynced) {
                try {
                    val map = mapOf(
                        "profileId" to log.profileId,
                        "timestamp" to log.timestamp,
                        "amountMl" to log.amountMl
                    )
                    val docRef = fs.collection("hydration").add(map).awaitTask()
                    hydrationDao.updateFirestoreId(log.id, docRef.id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // log failed sync, but don't fail whole worker
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
