package com.vkm.healthmonitor.compose.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import android.app.NotificationManager
import com.vkm.healthmonitor.compose.MainActivity
import com.vkm.healthmonitor.R

object NotificationUtil {
    fun showHydrationReminder(ctx: Context, profileId: Int) {
        val quickIntent = Intent("com.vkm.healthmonito.ACTION_ADD_250ML").apply { putExtra("profileId", profileId) }
        val pendingQuick = PendingIntent.getBroadcast(ctx, profileId, quickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val openAppIntent = Intent(ctx, MainActivity::class.java)
        val pendingOpen = PendingIntent.getActivity(ctx, profileId + 1000, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(ctx, "reminders")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminder")
            .setContentText("Add 250 ml for your profile")
            .setContentIntent(pendingOpen)
            .addAction(com.vkm.healthmonitor.R.drawable.ic_water, "Add 250ml", pendingQuick)
            .setAutoCancel(true)
            .build()

        val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(2000 + profileId, notif)
    }

    fun showPlanNotification(ctx: Context, title: String, text: String) {
        val openAppIntent = Intent(ctx, MainActivity::class.java)
        val pendingOpen = PendingIntent.getActivity(ctx, title.hashCode(), openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(ctx, "reminders")
            .setSmallIcon(R.drawable.ic_guide)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingOpen)
            .setAutoCancel(true)
            .build()
        val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(3000 + title.hashCode(), notif)
    }
}

