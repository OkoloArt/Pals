package com.example.helloworld.common.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.helloworld.common.Constants.APP_KEY
import com.example.helloworld.common.Constants.ENVIRONMENT
import com.example.helloworld.common.Constants.FCM_SENDER_ID
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.domain.use_cases.ProfileUseCase
import com.sinch.android.rtc.*
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallController
import com.sinch.android.rtc.calling.CallControllerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SinchService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    var currentToken : String? = null
    private var listener: StartFailedListener? = null

    @Inject
    lateinit var userPreferences: UserPreferences

    companion object{
        var sinchClient: SinchClient? = null
    }

    private fun createClient(username: String) {
        serviceScope.launch {
            userPreferences.token.collect { token ->
                currentToken = token
            }
        }
        sinchClient = SinchClient.builder().context(applicationContext)
            .userId(username)
            .applicationKey(APP_KEY)
            .environmentHost(ENVIRONMENT)
            .pushConfiguration(
                    PushConfiguration.fcmPushConfigurationBuilder()
                        .senderID(FCM_SENDER_ID)
                        .registrationToken(currentToken.orEmpty())
                        .build()
            )
            .pushNotificationDisplayName("User \$username")
            .build()
        sinchClient?.addSinchClientListener(MySinchClientListener())
        sinchClient?.callController?.addCallControllerListener(SinchCallControllerListener())
    }

    override fun onBind(intent: Intent?): IBinder {
        return SinchServiceInterface()
    }

    inner class SinchServiceInterface : Binder() {

        fun startClient() {
            // The username is fetched from settings.
           // start()
        }

        fun setStartListener(listener: StartFailedListener?) {
            this@SinchService.listener = listener
        }
    }
}


interface StartFailedListener {
    fun onFailed(error: SinchError)
    fun onStarted()
}

class SinchCallControllerListener : CallControllerListener {
    override fun onIncomingCall(callController: CallController , call: Call) {
        TODO("Not yet implemented")
    }

}

class MySinchClientListener : SinchClientListener {
    override fun onClientFailed(client: SinchClient , error: SinchError) {
        TODO("Not yet implemented")
    }

    override fun onClientStarted(client: SinchClient) {
        TODO("Not yet implemented")
    }

    override fun onCredentialsRequired(clientRegistration: ClientRegistration) {
        TODO("Not yet implemented")
    }

    override fun onLogMessage(level: Int , area: String , message: String) {
        TODO("Not yet implemented")
    }

    override fun onPushTokenRegistered() {
        TODO("Not yet implemented")
    }

    override fun onPushTokenRegistrationFailed(error: SinchError) {
        TODO("Not yet implemented")
    }

    override fun onPushTokenUnregistered() {
        TODO("Not yet implemented")
    }

    override fun onPushTokenUnregistrationFailed(error: SinchError) {
        TODO("Not yet implemented")
    }

    override fun onUserRegistered() {
        TODO("Not yet implemented")
    }

    override fun onUserRegistrationFailed(error: SinchError) {
        TODO("Not yet implemented")
    }

}
