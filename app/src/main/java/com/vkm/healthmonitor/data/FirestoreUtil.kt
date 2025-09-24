package com.vkm.healthmonitor.data
import com.vkm.healthmonitor.model.HealthEntry
import com.vkm.healthmonitor.model.HydrationEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
object FirestoreUtil {
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    fun addHealth(entry: HealthEntry) = db.collection("health").add(entry)
    fun healthQuery(): Query = db.collection("health").orderBy("timestamp")
    fun addHydration(entry: HydrationEntry) = db.collection("hydration").add(entry)
    fun hydrationQueryToday(start:Long,end:Long): Query =
        db.collection("hydration").whereGreaterThanOrEqualTo("timestamp", start).whereLessThan("timestamp", end)
}
