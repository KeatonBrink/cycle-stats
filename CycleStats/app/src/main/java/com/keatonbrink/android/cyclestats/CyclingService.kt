package com.keatonbrink.android.cyclestats

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class CyclingService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.name -> {
                startTracking()
            }
            Actions.STOP.name -> {
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTracking() {
        val notification = NotificationCompat.Builder(this, "cycling_channel")
            .setContentTitle("Cycling Service")
//            TODO: add the start time of the trip as text
            .setContentText("Tracking your cycling")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
    }

    enum class Actions {
        START,
        STOP
    }
}