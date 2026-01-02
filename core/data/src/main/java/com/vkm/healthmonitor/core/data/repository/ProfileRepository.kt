package com.vkm.healthmonitor.core.data.repository


import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.model.CurrentSelection
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.model.ProfileWithVitals
import com.vkm.healthmonitor.core.common.awaitTask
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ProfileRepository @Inject constructor(
    private val db: AppDatabase,
    private val fs: FirebaseFirestore,
    @ApplicationContext private val ctx: Context
) {

    private val profileDao = db.profileDao()
    private val selectionDao = db.selectionDao()

    fun allProfilesFlow(): Flow<List<Profile>> = profileDao.allProfiles()

    suspend fun insertOrUpdate(profile: Profile): Long = withContext(Dispatchers.IO) {
        val bmi = Profile.computeBmi(profile.heightCm, profile.weightKg)
        val toSave = profile.copy(bmi = bmi.coerceAtLeast(0f)) // never NaN
        val id = if (toSave.id == 0) profileDao.insert(toSave) else {
            profileDao.update(toSave)
            toSave.id.toLong()
        }
        // push to Firestore (best-effort)
        try {
            fs.collection("profiles").document(id.toString()).set(toSave).awaitTask()
        } catch (_: Exception) {}
        id
    }


    suspend fun deleteProfile(profile: Profile) = withContext(Dispatchers.IO) {
        profileDao.delete(profile)
        try { fs.collection("profiles").document(profile.id.toString()).delete().awaitTask() } catch (_: Exception) {}
    }

    suspend fun getById(id: Int): Profile? = withContext(Dispatchers.IO) { profileDao.getById(id) }

    // current selected profile id (single-row table)
    fun currentProfileIdFlow() = selectionDao.currentProfileIdFlow()

    suspend fun setCurrentProfile(id: Int) = withContext(Dispatchers.IO) {
        selectionDao.upsert(CurrentSelection(profileId = id))
    }

    // refresh local from server (one-time)
    suspend fun refreshProfilesFromServer() = withContext(Dispatchers.IO) {
        try {
            val snap = fs.collection("profiles").get().awaitTask()
            snap.documents.forEach { doc ->
                val map = doc.data ?: return@forEach
                val id = doc.id.toIntOrNull() ?: 0
                val p = Profile(
                    id = id,
                    name = map["name"] as? String ?: "",
                    age = (map["age"] as? Long)?.toInt() ?: (map["age"] as? Int ?: 0),
                    gender = map["gender"] as? String ?: "",
                    heightCm = (map["heightCm"] as? Double)?.toFloat() ?: (map["heightCm"] as? Long)?.toFloat() ?: 0f,
                    weightKg = (map["weightKg"] as? Double)?.toFloat() ?: (map["weightKg"] as? Long)?.toFloat() ?: 0f,
                    dailyWaterGoalMl = (map["dailyWaterGoalMl"] as? Long)?.toInt() ?: (map["dailyWaterGoalMl"] as? Int ?: 2000),
                    bmi = (map["bmi"] as? Double)?.toFloat() ?: 0f,
                    relationTo = map["relationTo"] as? String,
                    dailyStepGoal = (map["dailyStepGoal"] as? Long)?.toInt() ?: 10000,
                    dailySleepGoalHours = (map["dailySleepGoalHours"] as? Double)?.toFloat() ?: 8f,
                    caffeineSensitivity = map["caffeineSensitivity"] as? String ?: "Medium"
                )
                profileDao.insert(p)
            }
        } catch (_: Exception) {}
    }

    fun getProfiles() = profileDao.getAllProfiles()
    fun getProfilesWithVitals() = profileDao.getAllWithVitals()

    fun getProfilesFlow(): Flow<List<Profile>> = profileDao.getAllProfiles()

    fun getProfilesWithVitalsFlow(): Flow<List<ProfileWithVitals>> = profileDao.getAllWithVitals()
}
