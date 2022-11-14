package com.saket.mysampleservice

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val myReceiver = MyReceiver()
    lateinit var serviceIntent: Intent
    //onCreate is for setting up work.
    //however, in this case it is called only first couple of times the app icon is clicked...
    //after that only onStart is called. So moving registering logic to onStart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textMessage = findViewById<TextView>(R.id.txtMessage)
        val broadcastButton = findViewById<Button>(R.id.btnBroadcast)
        //var count = 0
        //textMessage.setText("Hello ${count++}")

        broadcastButton.setOnClickListener {
            Log.e(TAG, "Saket On Click called")
            /*
            val testIntent = Intent()
            testIntent.component = ComponentName("com.aptiv.interiorsensingwidget","com.aptiv.interiorsensingwidget.InteriorSensingWidget")
            testIntent.action = "com.aptiv.weatherwidget.ACTION_TEST_RESULT"
            println("Saket sending broadcast...")
            applicationContext.sendBroadcast(testIntent)
             */
        }

        //Log.e(TAG, "Saket MainActivity onCreate")
        serviceIntent = Intent(applicationContext, MyStartService::class.java)
        startService(serviceIntent)
        finish()
    }

    //onStart is called after onCreate, and it is called each time
    //user clicks on app icon
    override fun onStart() {
        super.onStart()
        //val intentFilter = IntentFilter("com.saket.mysampleservice.MY_NOTIFICATION")
        //registerReceiver(myReceiver,intentFilter)
        Log.e(TAG, "Saket onStart called")
    }

    //On stop is called just before onDestroy. It seems to be called each time.
    //So it seems a safer place to unregister the receiver
    override fun onStop() {
        super.onStop()
        Log.e(TAG, "Saket onStop called")
    }

    //onDestroy is not guaranteed to be called every time.
    //In our case, it is called only first time.
    override fun onDestroy() {
        super.onDestroy()
        //stopService(serviceIntent)
        Log.e(TAG, "Saket MainActivity onDestroy")
    }
}