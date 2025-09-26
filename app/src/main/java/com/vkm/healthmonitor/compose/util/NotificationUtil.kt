package com.vkm.healthmonitor.compose.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.vkm.healthmonitor.R
import com.vkm.healthmonitor.compose.MainActivity
import com.vkm.healthmonitor.compose.receiver.HydrationQuickAddReceiver
import com.vkm.healthmonitor.constant.AppConstants
import kotlin.random.Random

object NotificationUtil {
    fun showHydrationReminder(ctx: Context, profileId: Int) {
        val quickIntent = Intent(ctx, HydrationQuickAddReceiver::class.java).apply { putExtra(
            AppConstants.WORK_DATA_KEY_PROFILE_ID, profileId)
            putExtra(AppConstants.WORK_DATA_KEY_AMOUNT_ML, "250")
            action = "com.vkm.healthmonitor.ACTION_ADD_250ML"
        }

        val pendingQuick = PendingIntent.getBroadcast(ctx, profileId, quickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val openAppIntent = Intent(ctx, MainActivity::class.java)
        openAppIntent.putExtra(AppConstants.WORK_DATA_KEY_PROFILE_ID, profileId)
        openAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        openAppIntent.putExtra(AppConstants.HYDRATION_ACTION, AppConstants.HYDRATION_ACTION)
        val pendingOpen = PendingIntent.getActivity(ctx, profileId + 1000, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(ctx, "reminders")
            .setSmallIcon(R.drawable.ic_water)
            .setContentTitle("Hydration Reminder")
            .setContentText(getRandomMessage())
            .setContentIntent(pendingOpen)
            .addAction(R.drawable.ic_water, "Add 250ml", pendingQuick)
            .setAutoCancel(true)
            .build()

        val mgr = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mgr.notify(2000 + profileId, notif)
    }

    val HYDRATION_MESSAGES = listOf<String>(
        "This is your personal hydration coach speaking. Go get some H2O!\uD83D\uDCA7",
        "I'm just a notification, standing in front of a human, asking them to drink water.",
        "Fuel your mind, body, and soul. Drink some water.",
        "H2-Oh-Yeah! It's time for a sip.",
        "Hello there, just a friendly splash to remind you to drink some water!",
        "Time to hydrate! \uD83D\uDCA7", "" +
                "Water you doing? Go drink some water!",
        "C'mon, let's keep that body happy and hydrated!",
        "\"Don't forget to hydrate!\"—sent with love from your body.",
        "The best version of you is a hydrated version of you.",
        "Your cells are calling. They need a drink.",
        "Relationship status: In a committed relationship with my water bottle. Time for a date!",
        "Keep calm and stay hydrated.",
        "Get that glow! ✨ Time for some water.",
        "Every drop counts. Have a sip!",
        "Your body is thirsty. Drink up!",
        "Water: because adulting is hard, and juice boxes are for amateurs.",
        "A little reminder from your future self. (You'll thank me later!)",
        "Don't be a prune. Get hydrated!",
        "A reminder to drink water is a reminder to take care of yourself.",
        "Don't be salty. Drink some water.",
        "Water break!", "Your water bottle misses you.", "Sip, sip, hooray!"
    )

    fun getRandomMessage(): String {
        val random = Random.nextInt(HYDRATION_MESSAGES.size)
        return HYDRATION_MESSAGES[random]
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

