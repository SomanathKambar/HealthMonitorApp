package com.vkm.healthmonitor.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vkm.healthmonitor.core.model.CurrentSelection
import com.vkm.healthmonitor.core.model.DailyEnergyScore
import com.vkm.healthmonitor.core.model.EnergyAdjustment
import com.vkm.healthmonitor.core.model.HealthPlan
import com.vkm.healthmonitor.core.model.HealthStandard
import com.vkm.healthmonitor.core.model.HydrationLog
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.model.VitalEntry

@Database(
    entities = [Profile::class, VitalEntry::class, HydrationLog::class, HealthStandard::class, HealthPlan::class, CurrentSelection::class, DailyEnergyScore::class, EnergyAdjustment::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun vitalsDao(): VitalDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun standardsDao(): StandardsDao
    abstract fun plansDao(): HealthPlanDao
    abstract fun selectionDao(): CurrentSelectionDao
    abstract fun energyScoreDao(): EnergyScoreDao
    abstract fun energyAdjustmentDao(): EnergyAdjustmentDao

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
