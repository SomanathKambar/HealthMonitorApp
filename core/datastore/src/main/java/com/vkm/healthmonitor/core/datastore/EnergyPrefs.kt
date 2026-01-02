package com.vkm.healthmonitor.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "energy_prefs")

@Singleton
class EnergyPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val WAKE_TIME = longPreferencesKey("manual_wake_time")
    private val SLEEP_QUALITY = stringPreferencesKey("manual_sleep_quality")

    val wakeTime: Flow<Long?> = context.dataStore.data.map { it[WAKE_TIME] }
    
    suspend fun saveWakeTime(timestamp: Long) {
        context.dataStore.edit { it[WAKE_TIME] = timestamp }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
