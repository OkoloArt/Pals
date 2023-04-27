package com.example.helloworld

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application(){

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
}