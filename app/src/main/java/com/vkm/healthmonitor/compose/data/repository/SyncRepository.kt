package com.vkm.healthmonitor.compose.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.model.VitalEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncRepository @Inject constructor(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) {
    fun syncProfiles() {
        firestore.collection("profiles").addSnapshotListener { snap, _ ->
            snap?.documents?.mapNotNull { it.toObject(Profile::class.java) }?.forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    db.profileDao().insert(it)
                }
            }
        }
    }

    fun syncVitals(profileId: Int) {
        firestore.collection("vitals")
            .whereEqualTo("profileId", profileId)
            .addSnapshotListener { snap, _ ->
                snap?.documents?.mapNotNull { it.toObject(VitalEntry::class.java) }?.forEach {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.vitalsDao().insert(it)
                    }
                }
            }
    }

    suspend fun pushVital(v: VitalEntry) {
        firestore.collection("vitals").document(v.id.toString()).set(v)
    }

    suspend fun pushProfile(p: Profile) {
        firestore.collection("profiles").document(p.id.toString()).set(p)
    }
}
