package com.saket.mysampleservice

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "MyStartService"

class MyStartService : Service() {
    private val myReceiver = MyReceiver()

    override fun onCreate() {
        Log.e(TAG, "Saket MyStartService onCreate")
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.e(TAG, "Saket MyStartService onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Saket MyStartService onStartCommand")
        val intentFilter = IntentFilter("com.saket.mysampleservice.MY_NOTIFICATION")
        registerReceiver(myReceiver, intentFilter)

        val testIntent = Intent()
        //Below line is only required for manifest registered BR
        //testIntent.component = ComponentName(packageName,"com.saket.mysampleservice.MyReceiver")
        testIntent.action = "com.saket.mysampleservice.MY_NOTIFICATION"
        testIntent.putExtra("data", "Hello World!")
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        coroutineScope.launch {
            /*
            For delay of 5 secs, service is allowed to launch activity
            But for delay of 50 secs, system aborts launch activity
            from background.
            This behavior is same irrespective of whether finish() is called
            in MainActivity or not....
             */
            /*delay(5000)
            val activityIntent = Intent(baseContext, DummyActivity::class.java)
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(activityIntent)
            Log.e(TAG, "Saket Launched activity from Service...")
            */

            /*
            However, long delay has NO AFFECT when sending
            a broadcast. Even after MainActivity is finished...
             */
            delay(50000)
            sendBroadcast(testIntent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        myReceiver.stopService()
        unregisterReceiver(myReceiver)
        Log.e(TAG, "Saket MyStartService onDestroy")
    }
}
