package com.example.helloworld.common.services

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.helloworld.common.Constants.APP_KEY
import com.example.helloworld.common.Constants.APP_SECRET
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.Constants.ENVIRONMENT
import com.example.helloworld.common.Constants.FCM_SENDER_ID
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.ui.fragments.CallScreenFragment
import com.example.helloworld.ui.fragments.IncomingCallFragment
import com.sinch.android.rtc.*
import com.sinch.android.rtc.Internals.terminateForcefully
import com.sinch.android.rtc.calling.*
import com.sinch.android.rtc.sample.push.JWT.create
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


class SinchService : Service() {

    companion object {
        private val TAG = SinchService::class.java.simpleName

        const val ENVIRONMENT = "ocra.api.sinch.com"

        const val MESSAGE_PERMISSIONS_NEEDED = 1
        const val REQUIRED_PERMISSION = "REQUIRED_PERMISSION"
        const val MESSENGER = "MESSENGER"
        const val CALL_ID = "CALL_ID"
    }

    private val settings: PersistedSettings by lazy {
        PersistedSettings(applicationContext)
    }
    private val sinchServiceInterface: SinchServiceInterface = SinchServiceInterface()

    private var messenger: Messenger? = null
    private var sinchClient: SinchClient? = null
    private var listener: StartFailedListener? = null
    private var userId: String? = null

    override fun onCreate() {
        super.onCreate()
        attemptAutoStart()
    }

    private fun attemptAutoStart() {
        if (messenger != null) {
            start()
        }
    }
    

    private fun createClient(username: String) {
        userId = username
        sinchClient = SinchClient.builder().context(applicationContext)
            .userId(username)
            .applicationKey(APP_KEY)
            .environmentHost(ENVIRONMENT)
            .pushNotificationDisplayName("User $username")
            .build()
        sinchClient?.addSinchClientListener(MySinchClientListener())
        sinchClient?.callController?.addCallControllerListener(SinchCallControllerListener())
    }

    override fun onDestroy() {
        if (sinchClient?.isStarted == true) {
            sinchClient?.terminateGracefully()
        }
        super.onDestroy()
    }

    private fun hasUsername(): Boolean {
        if (settings.username.isEmpty()) {
            Log.e(TAG, "Can't start a SinchClient as no username is available!")
            return false
        }
        return true
    }

    private fun createClientIfNecessary() {
        if (sinchClient != null) {
            return
        }
        check(hasUsername()) { "Can't create a SinchClient as no username is available!" }
        createClient(settings.username)
    }

    private fun start() {
        var permissionsGranted = true
        try {
            createClientIfNecessary()
            //mandatory checks
            sinchClient?.checkManifest()
        } catch (e: MissingPermissionException) {
            permissionsGranted = false
            if (messenger != null) {
                val message = Message.obtain()
                val bundle = Bundle()
                bundle.putString(REQUIRED_PERMISSION, e.requiredPermission)
                message.data = bundle
                message.what = MESSAGE_PERMISSIONS_NEEDED
                messenger?.send(message)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to create SinchClient ", e)
            return
        }
        if (permissionsGranted) {
            Log.d(TAG, "Starting SinchClient")
            try {
                sinchClient?.start()
            } catch (e: IllegalStateException) {
                Log.w(TAG, "Can't start SinchClient - " + e.message)
            }
        }
    }

    private fun stop() {
        sinchClient?.terminateGracefully()
        sinchClient = null
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = intent.getParcelableExtra(MESSENGER)
        return sinchServiceInterface
    }

    interface StartFailedListener {
        fun onFailed(error: SinchError)
        fun onStarted()
    }

    inner class SinchServiceInterface : Binder() {

        fun callUser(userId: String): Call {
            val sinchClient = sinchClient ?: throw RuntimeException("Sinch Client is not created")
            return sinchClient.callController.callUser(userId, MediaConstraints(false))
        }

        var username: String
            get() = settings.username
            set(value) {
                settings.username = value
            }

        fun retryStartAfterPermissionGranted() {
            attemptAutoStart()
        }

        val isStarted: Boolean
            get() = sinchClient?.isStarted == true

        fun startClient() {
            start()
        }

        fun stopClient() {
            stop()
        }

        fun unregisterPushToken() {
            sinchClient?.unregisterPushToken(SimplePushTokenUnregistrationCallback())

        }

        fun setStartListener(listener: StartFailedListener?) {
            this@SinchService.listener = listener
        }

        fun getCall(callId: String): Call? {
            return sinchClient?.callController?.getCall(callId)
        }

        val audioController: AudioController?
            get() = sinchClient?.audioController

        fun relayRemotePushNotificationPayload(result: CallNotificationResult) {
            if (!hasUsername()) {
                Log.e(TAG, "Unable to relay the push notification!")
                return
            }
            createClientIfNecessary()
            sinchClient?.relayRemotePushNotification(result)
        }
    }

    private inner class MySinchClientListener : SinchClientListener {

        override fun onClientFailed(client: SinchClient, error: SinchError) {
            listener?.onFailed(error)
            terminateForcefully(sinchClient)
            sinchClient = null
        }

        override fun onClientStarted(client: SinchClient) {
            Log.d(TAG, "SinchClient started")
            listener?.onStarted()
        }

        override fun onLogMessage(level: Int, area: String, message: String) {
            when (level) {
                Log.DEBUG -> Log.d(area, message)
                Log.ERROR -> Log.e(area, message)
                Log.INFO -> Log.i(area, message)
                Log.VERBOSE -> Log.v(area, message)
                Log.WARN -> Log.w(area, message)
                else -> {}
            }
        }

        override fun onCredentialsRequired(clientRegistration: ClientRegistration) {
            clientRegistration.register(create(APP_KEY, APP_SECRET, userId.orEmpty()))
        }

        override fun onUserRegistered() {
            Log.d(TAG, "User registered.")
        }

        override fun onUserRegistrationFailed(error: SinchError) {
            Log.e(TAG, "User registration failed: " + error.message)
        }

        override fun onPushTokenRegistered() {
            Log.d(TAG, "Push token registered.")
        }

        override fun onPushTokenRegistrationFailed(error: SinchError) {
            Log.e(TAG, "Push token registration failed." + error.message)
        }

        override fun onPushTokenUnregistered() {
            Log.d(TAG, "Push token unregistered.")
        }

        override fun onPushTokenUnregistrationFailed(error: SinchError) {
            Log.e(TAG, "Push token unregistration failed." + error.message)
        }
    }

    private inner class SinchCallControllerListener : CallControllerListener {

        override fun onIncomingCall(callController: CallController, call: Call) {
        }

        private fun isAppOnForeground(context: Context): Boolean {
            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance
                    == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName == packageName
                ) {
                    return true
                }
            }
            return false
        }
    }

    private inner class PersistedSettings(context: Context) {
        private val prefKey = "Sinch"
        private val store: SharedPreferences = context.getSharedPreferences(prefKey, MODE_PRIVATE)

        var username: String
            get() = store.getString("Username", "").orEmpty()
            set(username) {
                store.edit()
                    .putString("Username", username)
                    .commit()
            }
    }

}

class SimplePushTokenUnregistrationCallback : PushTokenUnregistrationCallback {
    override fun onPushTokenUnregistered() {
        Log.d(TAG, "Successfully unregistered Push Token!")
    }

    override fun onPushTokenUnregistrationFailed(error: SinchError) {
        Log.e(TAG, "Unregistration of the Push Token failed! " + error.message)
    }

    companion object {
        var TAG: String = SimplePushTokenUnregistrationCallback::class.java.simpleName
    }
}






