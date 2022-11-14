package com.saket.mysampleservice

import android.content.*
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.saket.mysampleservice.workmanager.ExpeditedWorker
import com.saket.mysampleservice.workmanager.MyBackgroundCountingWork

private const val TAG = "MyBroadcastReceiver"

class MyReceiver : BroadcastReceiver() {

    lateinit var serviceIntent: Intent
    lateinit var foregroundServiceIntent: Intent
    lateinit var mContext: Context

    val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as MyService.MyBinder
            val service = binder.getService()
            service.startPrinting()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.e(TAG, "Saket onServiceDisconnected")
        }

    }

    //@RequiresApi(Build.VERSION_CODES.S) //This enables to use context.isUiContext
    override fun onReceive(context: Context, intent: Intent) {

        //Log.e(TAG, "Saket onReceive context is UIContext ${context.isUiContext}")
        Log.e(TAG, "Saket onReceive ")
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                //Below line does not work on API 31 device
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
        mContext = context
        serviceIntent = Intent(context, MyService::class.java)
        foregroundServiceIntent = Intent(context, SampleForegroundService::class.java)
        //context.startService(serviceIntent)

        /*
        How am i able to bind service in onReceive???
        From BR doc -
        Note: Only activities, services, and content providers can bind to a service—you can't
        bind to a service from a broadcast receiver.
        However, a dynamic registered receiver can bind to a service as its
        lifecycle is associated with the component that registered the receiver. Which
        would be MyStartService in this case..
        But Manifest registered receiver cannot bind to a service. It requires
        a workaround to achieve the same result. It has to use an application Context
        when calling bindService. Otherwise it gives this error -
        java.lang.RuntimeException: Unable to start receiver com.saket.mysampleservice.MyReceiver:
        android.content.ReceiverCallNotAllowedException: BroadcastReceiver components are not
        allowed to bind to services.
        For more details refer-
        https://medium.com/@debuggingisfun/android-o-work-around-background-service-limitation-e697b2192bc3
         */
        //context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)

        /*
        If startForegroundService is called immediately after calling activity goes into
        background (delay of 5 secs in Started Service) then it seems possible to start
        foreground service from Manifest registered receiver.

        But if it is called after delay 50 secs, then calling startForegroundService from
        Manifest registered BR throws exception  -
        java.lang.RuntimeException: Unable to start receiver
        com.saket.mysampleservice.MyReceiver: android.app.ForegroundServiceStartNotAllowedException:
        startForegroundService() not allowed due to mAllowStartForeground false: service
        com.saket.mysampleservice/.SampleForegroundService

        Same issue persists even if i change from Manifest registered
        BR to Context registered BR....
        W  Background started FGS: Disallowed [
        callingPackage: com.saket.mysampleservice; callingUid:
        1010166; uidState: SVC ;
        intent: Intent
        { cmp=com.saket.mysampleservice/.SampleForegroundService };
        code:DENIED; tempAllowListReason:<null>;
        targetSdkVersion:32;
        callerTargetSdkVersion:32;
        startForegroundCount:0;
        bindFromPackage:null]
        665-783   Compatibil...geReporter system_process
        D  Compat change id reported: 170668199; UID 1010166; state: ENABLED
        665-783   ActivityManager         system_process
        W  startForegroundService() not allowed due to mAllowStartForeground false:
        service com.saket.mysampleservice/.SampleForegroundService
         */
        //context.startForegroundService(foregroundServiceIntent)

        //startWork()
        //startExpediteLongWork()
    }

    fun startWork() {
        val countWorkRequest = OneTimeWorkRequestBuilder<MyBackgroundCountingWork>().build()
        WorkManager
            .getInstance(mContext)
            .enqueue(countWorkRequest)
    }

    private fun startExpediteLongWork() {
        val expeditedWorkRequest = OneTimeWorkRequestBuilder<ExpeditedWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager
            .getInstance(mContext)
            .enqueue(expeditedWorkRequest)
    }

    fun stopService() {
        //mContext.stopService(serviceIntent)
        //mContext.unbindService(connection)
        mContext.stopService(foregroundServiceIntent)
    }
}