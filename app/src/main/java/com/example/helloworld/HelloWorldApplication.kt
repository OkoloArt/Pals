package com.example.helloworld

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.FirebaseApp
import com.sinch.android.rtc.Sinch
import com.sinch.android.rtc.SinchClient
import dagger.hilt.android.HiltAndroidApp
import java.nio.channels.Pipe.SinkChannel

@HiltAndroidApp
class HelloWorldApplication : Application(){

    private var sinchClient: SinchClient? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID , name , importance).apply {
                description = descriptionText
            }
            channel.apply {
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initSinch(){

        // Create a SinchClient instance
        sinchClient = SinchClient.builder()
            .context(applicationContext)
            .userId(firebaseAuth.uid!!)
            .applicationKey("")
            .environmentHost("") // or your desired environment
            .build()

        // Optionally, you can set listener, call listener, and other configurations for the sinchClient here

        // Start the SinchClient
        sinchClient?.start()
    }
}