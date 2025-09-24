package com.vkm.healthmonitor.compose.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vkm.healthmonitor.compose.data.model.CurrentSelection
import com.vkm.healthmonitor.compose.data.model.HealthPlan
import com.vkm.healthmonitor.compose.data.model.HealthStandard
import com.vkm.healthmonitor.compose.data.model.HydrationLog
import com.vkm.healthmonitor.compose.data.model.Profile
import com.vkm.healthmonitor.compose.data.model.VitalEntry

@Database(
    entities = [Profile::class, VitalEntry::class, HydrationLog::class, HealthStandard::class, HealthPlan::class, CurrentSelection::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun vitalsDao(): VitalDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun standardsDao(): StandardsDao
    abstract fun plansDao(): HealthPlanDao
    abstract fun selectionDao(): CurrentSelectionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun get(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "health-db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
