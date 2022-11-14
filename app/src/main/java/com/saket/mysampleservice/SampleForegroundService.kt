package com.saket.mysampleservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "SampleForegroundService"
class SampleForegroundService : Service() {

    override fun onCreate() {
        Log.e(TAG, "SampleForegroundService onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(123, getNotification())
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        coroutineScope.launch {
            repeat(50) {
                index -> Log.e(TAG, index.toString())
                delay(1000)
            }
            Log.e(TAG, "Calling stop self")
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getNotification(): Notification {
        val name = getString(R.string.app_name)
        val descriptionText = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel_id = "com.example.mysampleservice"
        val channel = NotificationChannel(channel_id, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, channel_id)
            .setContentTitle("Sample music player")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(Icon.createWithResource(baseContext, R.drawable.ic_launcher_background))
            .build()
        return notification
    }

    override fun onDestroy() {
        Log.e(TAG, "SampleForegroundService onDestroy")
        super.onDestroy()
    }
}