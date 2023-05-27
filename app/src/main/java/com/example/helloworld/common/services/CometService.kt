package com.example.helloworld.common.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.*
import com.cometchat.pro.models.User
import com.cometchat.pro.rtc.core.CallAppSettings
import com.cometchat.pro.rtc.core.CometChatCalls
import com.cometchat.pro.rtc.exceptions.CometChatException


class CometService : Service(){

    override fun onBind(intent: Intent?): IBinder? {
       return CometServiceInterface()
    }

    override fun onCreate() {
        super.onCreate()
        initCometChat()
    }

    private fun createClient(uId : String){
        val user = User()
        user.uid = uId

        CometChat.createUser(user , AUTH_KEY , object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                // User creation successful
            }

            override fun onError(p0: com.cometchat.pro.exceptions.CometChatException?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun start(uId: String){
        createClient(uId)
    }


    inner class CometServiceInterface : Binder() {

        fun callUser(receiverID: String) {
            val receiverType = CometChatConstants.RECEIVER_TYPE_USER
            val callType = CometChatConstants.CALL_TYPE_VIDEO

            val call = Call(receiverID,receiverType,callType)

            CometChat.initiateCall(call , object : CometChat.CallbackListener<Call>() {
                override fun onSuccess(p0: Call?) {
                    TODO("Not yet implemented")
                }

                override fun onError(p0: com.cometchat.pro.exceptions.CometChatException?) {
                    TODO("Not yet implemented")
                }
            })
        }

        fun startClient(uId: String) {
            start(uId)
        }

        fun stopClient() {
        }

    }

    private fun initCometChat(){
        val callAppSettings = CallAppSettings.CallAppSettingBuilder()
            .setAppId(appID)
            .setRegion(region).build()

        CometChatCalls.init(applicationContext, callAppSettings, object : CometChatCalls.CallbackListener<String>() {
            override fun onSuccess(s: String?) {

            }

            override fun onError(e: CometChatException) {

            }
        })
    }


    companion object{
        const val appID:String ="239994fd0610a543"  // Replace with your App ID
        const val region:String ="us"  // Replace with your App Region ("eu" or "us")
         val TAG: String = CometService::class.java.simpleName
        const val AUTH_KEY = "d3ed5af7f222f05dcf9aee8c180233fed96431d5"
    }

}