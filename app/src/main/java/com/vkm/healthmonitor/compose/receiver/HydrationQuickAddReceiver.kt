package com.vkm.healthmonitor.compose.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vkm.healthmonitor.compose.data.repository.HealthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HydrationQuickAddReceiver @Inject constructor(private val repo: HealthRepository) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val profileId = intent.getStringExtra("profile_id")
            val amount = intent.getStringExtra("amount")
            profileId?.let { id ->
                amount?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.insertHydration( id.toInt(), amount.toInt())
                    }
                }
            }
        }

    }
}
