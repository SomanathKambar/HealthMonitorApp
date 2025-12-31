package com.vkm.healthmonitor.compose.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.compose.data.db.AppDatabase
import com.vkm.healthmonitor.core.model.VitalEntry
import com.vkm.healthmonitor.core.common.awaitTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class VitalRepository @Inject constructor(
    private val db: AppDatabase,
    private val fs: FirebaseFirestore,
    @ApplicationContext private val ctx: Context
) {
    private val vitalsDao = db.vitalsDao()

    // Flow of all vitals for a profile
    fun getVitalsForProfile(profileId: Int): Flow<List<VitalEntry>> =
        vitalsDao.allForProfile(profileId)

    fun getVitalsForProfileByDate(profileId: Int, date: String): Flow<List<VitalEntry>> =
        vitalsDao.forProfileByDate(profileId, date) // replace with DAO query by date if present

    suspend fun insertVital(v: VitalEntry) = withContext(Dispatchers.IO) {
        // insert locally
        val id = vitalsDao.insert(v)
        // attempt upload to Firestore; store profileId and timestamp
        try {
            val map = mapOf(
                "profileId" to v.profileId,
                "timestamp" to v.timestamp,
                "date" to v.date,
                "pulse" to v.pulse,
                "bpSys" to v.bpSys,
                "bpDia" to v.bpDia,
                "temperature" to v.temperature,
                "spo2" to v.spo2
            )
            fs.collection("vitals").add(map).awaitTask()
        } catch (_: Exception) {}
        id
    }

    suspend fun deleteVital(v: VitalEntry) = withContext(Dispatchers.IO) {
        vitalsDao.delete(v)
        // Note: Ideally we also delete server doc if we saved id mapping; left as best-effort
    }

    // one-time: pull vitals for profile from server and upsert locally
    suspend fun refreshVitalsForProfile(profileId: Int) = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("vitals").whereEqualTo("profileId", profileId).get().awaitTask()
            snap.documents.forEach { doc ->
                val data = doc.data ?: return@forEach
                val v = VitalEntry(
                    profileId = (data["profileId"] as? Long)?.toInt() ?: (data["profileId"] as? Int ?: profileId),
                    timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis(),
                    date = data["date"] as? String ?: "",
                    pulse = (data["pulse"] as? Long)?.toInt() ?: 0,
                    bpSys = (data["bpSys"] as? Long)?.toInt() ?: 0,
                    bpDia = (data["bpDia"] as? Long)?.toInt() ?: 0,
                    temperature = (data["temperature"] as? Double)?.toFloat() ?: 0f,
                    spo2 = (data["spo2"] as? Long)?.toInt() ?: 0
                )
                vitalsDao.insert(v)
            }
        } catch (_: Exception) {}
    }

    fun vitalsFlowFor(profileId: Int): Flow<List<VitalEntry>> = vitalsDao.getVitalsForProfileFlow(profileId)
    suspend fun vitalsListFor(profileId: Int): List<VitalEntry> = vitalsDao.getVitalsForProfileList(profileId)
}
