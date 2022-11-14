package com.saket.mysampleservice.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.saket.mysampleservice.R
import kotlinx.coroutines.delay

/**
 * For long running tasks use a CoroutineWorker
 * CoroutineWorker provides a override fun getForegroundInfo
 * Call setForeground in doWork(). This will make it a
 * foreground service. Which can then work for long time,
 * which is more than 10 minutes.
 * Effectively, it seems similar to a FGS.
 * However, in this example since the work is executed in
 * background. So it throws similar exception that background cannot
 * launch FGS.
 */
private const val NOTIFICATION_ID = 124
private const val TAG = "ExpeditedWorker"

class ExpeditedWorker constructor(
    private val context: Context, params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    override suspend fun doWork(): Result {
        setForeground(getForegroundInfo())
        //Some long running task
        mylongtask()
        return Result.success()
    }

    private fun createNotification(): Notification {
        val name = context.getString(R.string.app_name)
        val descriptionText = context.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel_id = "com.example.mysampleservice"
        val channel = NotificationChannel(channel_id, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(context, channel_id)
            .setContentTitle("Sample music player")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(Icon.createWithResource(context, R.drawable.ic_launcher_background))
            .build()
        return notification
    }

    private suspend fun mylongtask() {
        for (i in 0..100) {
            Log.e(TAG, "Count in $i")
            delay(1000)
        }
    }
}