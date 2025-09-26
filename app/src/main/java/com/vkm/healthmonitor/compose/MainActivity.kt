package com.vkm.healthmonitor.compose

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vkm.healthmonitor.compose.ui.screens.MainAppScaffold
import com.vkm.healthmonitor.compose.util.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requiredPermissions = mutableListOf(
        Manifest.permission.POST_NOTIFICATIONS
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
               checkAndRequestPermissions()
            } else {
                createNotificationChannel()
                setupReminders()
            }
        } else  {
            createNotificationChannel()
            setupReminders()
        }

        setContent {
            MaterialTheme {
            MainAppScaffold()
        }
    }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("health_reminder_channel", "Health Reminders", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Reminders for hydration and vitals"
            (getSystemService(NotificationManager::class.java)).createNotificationChannel(channel)
        }
    }

    private fun setupReminders() {
        ReminderScheduler.scheduleWaterReminder(this)
        ReminderScheduler.scheduleVitalsReminder(this)
    }

    private fun checkAndRequestPermissions() {
        val notGranted = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 100)
        }
    }

}

