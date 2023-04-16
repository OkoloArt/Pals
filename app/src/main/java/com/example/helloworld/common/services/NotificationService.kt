package com.example.helloworld.common.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.helloworld.R
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.Constants.USERS
import com.example.helloworld.common.utils.FirebaseUtils
import com.example.helloworld.common.utils.FirebaseUtils.firebaseAuth
import com.example.helloworld.common.utils.FirebaseUtils.firebaseDatabase
import com.example.helloworld.data.model.User
import com.example.helloworld.ui.fragments.MessageFragmentArgs
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {

    private val receiver = User()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val map: Map<String, String> = remoteMessage.data
            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]
            val hisImage = map["hisImage"]
            val chatUid = map["chatId"]

            receiver.apply {
                username = title
                chatId = chatUid
                userId = hisId
                image = hisImage
            }

            val args = MessageFragmentArgs(receiver).toBundle()

            val pendingIntent = NavDeepLinkBuilder(applicationContext)
                .setGraph(R.navigation.nav_graph)
                .setArguments(args)
                .setDestination(R.id.messageFragment)
                .createPendingIntent()

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                createOreoNotification(title!!, message!!,pendingIntent)
            else createNormalNotification(title!!, message!!,pendingIntent)
        }
    }


    private fun updateToken(token: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(firebaseAuth.uid!!)
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)
    }

    private fun createNormalNotification(title: String, message: String,pendingIntent: PendingIntent) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uri)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(85 - 65), builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(title: String, message: String, pendingIntent: PendingIntent) {

        val channel = NotificationChannel(CHANNEL_ID, "Message", NotificationManager.IMPORTANCE_HIGH)
        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this , CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        manager.notify(100, notification)
    }

}
