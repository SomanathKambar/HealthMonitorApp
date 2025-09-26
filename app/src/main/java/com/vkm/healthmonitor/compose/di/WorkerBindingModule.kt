package com.vkm.healthmonitor.compose.di

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingModule {
    @Singleton
    @Binds
    abstract fun bindWorkerFactory(factory: HiltWorkerFactory): WorkerFactory
}
