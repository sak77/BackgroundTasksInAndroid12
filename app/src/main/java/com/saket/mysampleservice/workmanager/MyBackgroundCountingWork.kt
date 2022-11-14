package com.saket.mysampleservice.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val TAG = "MyBackgroundCountingWork"
class MyBackgroundCountingWork(
    context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
/*                    val testIntent = Intent()
                    testIntent.action = "android.appwidget.action.APPWIDGET_UPDATE"
                    testIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
                    applicationContext.sendBroadcast(testIntent)*/
        return Result.success()
    }

}