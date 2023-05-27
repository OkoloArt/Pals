package com.example.helloworld

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.cometchat.pro.rtc.core.CallAppSettings
import com.cometchat.pro.rtc.core.CometChatCalls
import com.cometchat.pro.rtc.exceptions.CometChatException
import com.example.helloworld.common.Constants
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.services.CometService
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.sinch.android.rtc.PushConfiguration
import com.sinch.android.rtc.SinchClient
import com.sinch.android.rtc.internal.natives.jni.CallClient
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class HelloWorldApplication : Application(){

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        initCometChat()
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


    private fun initCometChat(){
        val callAppSettings = CallAppSettings.CallAppSettingBuilder()
            .setAppId(CometService.appID)
            .setRegion(CometService.region).build()

        CometChatCalls.init(applicationContext , callAppSettings , object : CometChatCalls.CallbackListener<String>() {
            override fun onSuccess(s: String?) {

            }

            override fun onError(e: CometChatException) {

            }
        })
    }

}