package com.example.helloworld

import android.app.Application
import android.util.Log
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException

class HelloWorldApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        initializeCometChat()
    }

    private fun initializeCometChat(){
        val appID:String="231029536daec1f2"  // Replace with your App ID
        val region:String="us"  // Replace with your App Region ("eu" or "us")

        val appSetting = AppSettings.AppSettingsBuilder()
            .setRegion(region)
            .subscribePresenceForAllUsers()
            .autoEstablishSocketConnection(true)
            .build();

        CometChat.init(this , appID , appSetting , object : CometChat.CallbackListener<String>() {
            override fun onSuccess(p0: String?) {
                Log.d("TAG" , "Initialization completed successfully")
            }

            override fun onError(p0: CometChatException?) {
                Log.d("TAG" , "Initialization failed with exception: " + p0?.message)
            }

        })
    }
}