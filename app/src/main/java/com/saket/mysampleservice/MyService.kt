package com.saket.mysampleservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

private const val TAG = "MyService"

/**
 * lifecycle service gives instance of lifecycleScope.
 * LifecycleScope is automatically cancelled on destroy of lifecycle of containing class.
 */
//class MyService : LifecycleService() {
class MyService : Service() {
    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Default)
    lateinit var localJob: Job

    override fun onCreate() {
        Log.e(TAG, "Saket MyService onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Saket onStartCommand")

/*        lifecycleScope.launch {
            for (i in 0..100) {
                if (isActive) {
                    Log.e(TAG, "printing $i")
                    delay(1000)
                }
            }
        }*/

        localJob = coroutineScope.launch {
            for (i in 0..100) {
                if (isActive) {
                    Log.e(TAG, "printing $i")
                    delay(1000)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun printLogs() {
    }

    fun startPrinting() {
/*        lifecycleScope.launch {
            for (i in 0..100) {
                if (isActive) {
                    Log.e(TAG, "printing $i")
                    delay(1000)
                }
            }
        }*/
        localJob = coroutineScope.launch {
            for (i in 0..100) {
                if (isActive) {
                    Log.e(TAG, "printing $i")
                    delay(1000)
                }
            }
        }
    }

    inner class MyBinder : Binder() {
        fun getService(): MyService = this@MyService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG, "Saket onBind")
        return MyBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG, "Saket onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        localJob.cancel()
        Log.e(TAG, "Saket MyService onDestroy")
    }
}