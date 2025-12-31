package com.vkm.healthmonitor.compose.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vkm.healthmonitor.core.datastore.HydrationPrefs
import com.vkm.healthmonitor.constant.AppConstants
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HydrationQuickAddReceiver  : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val profileId = intent.getIntExtra(AppConstants.WORK_DATA_KEY_PROFILE_ID, -1)
            val amount = intent.getStringExtra(AppConstants.WORK_DATA_KEY_AMOUNT_ML)
            profileId?.let { id ->
                amount?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        val appContext = context.applicationContext
                        val entryPoint = EntryPointAccessors.fromApplication(
                            appContext,
                            HydrationReceiverEntryPoint::class.java
                        )
                        val repo = entryPoint.hydrationRepository()
                        repo.tryAddHydration( id.toInt(), amount.toInt())
                        HydrationPrefs.setLastReminderTime(context, id.toInt(), System.currentTimeMillis())
                    }
                }
            }
        }

    }
}
