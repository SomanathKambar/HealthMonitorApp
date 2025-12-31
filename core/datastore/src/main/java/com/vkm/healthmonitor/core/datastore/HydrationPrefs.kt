package com.vkm.healthmonitor.core.datastore

import android.content.Context
import androidx.core.content.edit

object HydrationPrefs {
    fun setLastReminderTime(ctx: Context, profileId: Int, timestamp: Long) {
        val prefs = ctx.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        prefs.edit { putLong("last_reminder_$profileId", timestamp) }
    }

    fun getLastReminderTime(ctx: Context, profileId: Int): Long {
        val prefs = ctx.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)
        return prefs.getLong("last_reminder_$profileId", 0L)
    }
}
