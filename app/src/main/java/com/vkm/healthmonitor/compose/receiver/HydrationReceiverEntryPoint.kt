package com.vkm.healthmonitor.compose.receiver

import com.vkm.healthmonitor.core.data.repository.HydrationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HydrationReceiverEntryPoint {
    fun hydrationRepository(): HydrationRepository
}
