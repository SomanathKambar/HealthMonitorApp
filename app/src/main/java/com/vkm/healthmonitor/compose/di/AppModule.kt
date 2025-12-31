package com.vkm.healthmonitor.compose.di

import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.data.repository.HealthRepository
import com.vkm.healthmonitor.core.data.repository.HydrationRepository
import com.vkm.healthmonitor.core.data.repository.PlanRepository
import com.vkm.healthmonitor.core.data.repository.ProfileRepository
import com.vkm.healthmonitor.core.data.repository.VitalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase = AppDatabase.get(ctx)
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideProfileRepo(db: AppDatabase, fs: FirebaseFirestore, @ApplicationContext ctx: Context) =
        ProfileRepository(db, fs, ctx)

    @Provides
    @Singleton
    fun provideVitalRepo(db: AppDatabase, fs: FirebaseFirestore, @ApplicationContext ctx: Context) =
        VitalRepository(db, fs, ctx)

    @Provides
    @Singleton
    fun provideHydrationRepo(db: AppDatabase, fs: FirebaseFirestore, @ApplicationContext ctx: Context) =
        HydrationRepository(db, fs, ctx)

    @Provides
    @Singleton
    fun providePlanRepo(db: AppDatabase, fs: FirebaseFirestore, @ApplicationContext ctx: Context) =
        PlanRepository(db, fs, ctx)

    @Provides
    @Singleton
    fun provideHealthRepo(db: AppDatabase, fs: FirebaseFirestore, @ApplicationContext ctx: Context) =
        HealthRepository(db, fs, WorkManager.getInstance(ctx), ctx)
}