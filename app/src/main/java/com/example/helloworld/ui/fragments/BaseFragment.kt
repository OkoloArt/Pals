package com.example.helloworld.ui.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.helloworld.common.services.SinchService

abstract class BaseFragment : Fragment(), ServiceConnection {

    protected var sinchServiceInterface: SinchService.SinchServiceInterface? = null
        private set

    private val messenger = Messenger(object : Handler(Looper.getMainLooper()) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SinchService.MESSAGE_PERMISSIONS_NEEDED -> {
                    val requiredPermission = msg.data.getString(SinchService.REQUIRED_PERMISSION)
                    requestPermissions(
                            arrayOf(
                                    requiredPermission,
                                    Manifest.permission.POST_NOTIFICATIONS
                            ), 0
                    )
                }
                else -> {}
            }
        }
    })

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        bindService()
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity?.window?.addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        bindService()
    }
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        if (SinchService::class.java.name == componentName.className) {
            sinchServiceInterface = iBinder as SinchService.SinchServiceInterface
            onServiceConnected()
        }
    }

    protected open fun onServiceConnected() {
        // for subclasses
    }

    override fun onServiceDisconnected(componentName: ComponentName) {
        if (SinchService::class.java.name == componentName.className) {
            sinchServiceInterface = null
            onServiceDisconnected()
        }
    }

    protected fun onServiceDisconnected() {
        // for subclasses
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var granted = grantResults.isNotEmpty()
        for (grantResult in grantResults) {
            granted = granted and (grantResult == PackageManager.PERMISSION_GRANTED)
        }
        if (granted) {
            Toast.makeText(activity, "You may now place a call", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                    activity,
                    "This application needs permission to use your microphone and camera to function "
                            + "properly.",
                    Toast.LENGTH_LONG
            ).show()
        }
        sinchServiceInterface?.retryStartAfterPermissionGranted()
    }

    private fun bindService() {
        val serviceIntent = Intent(activity, SinchService::class.java).apply {
            putExtra(SinchService.MESSENGER, messenger)
        }
        activity?.applicationContext?.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

}
