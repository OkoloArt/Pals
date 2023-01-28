package com.example.helloworld

import android.app.Application
import android.provider.SyncStateContract.Constants
import android.util.Log
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HelloWorldApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        initializeCometChat()
    }

    private fun initializeCometChat(){
        val appSetting = AppSettings.AppSettingsBuilder()
            .setRegion(com.example.common.Constants.REGION)
            .subscribePresenceForAllUsers()
            .autoEstablishSocketConnection(true)
            .build();

        CometChat.init(this , com.example.common.Constants.APP_ID , appSetting , object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d("TAG" , "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG" , "Initialization failed with exception: " + p0?.message)
            }

        })
    }
}