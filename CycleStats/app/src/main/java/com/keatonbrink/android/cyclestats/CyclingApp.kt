package com.keatonbrink.android.cyclestats

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class CyclingApp: Application() {
    override fun onCreate() {
        super.onCreate()

        TripRepository.initialize(this)

        // Set up the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "cycling_channel",
                "Cycling Notifications",
                NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}