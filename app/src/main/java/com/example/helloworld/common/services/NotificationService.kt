package com.example.helloworld.common.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.navigation.NavDeepLinkBuilder
import com.example.helloworld.R
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.Constants.CHAT_ID
import com.example.helloworld.common.Constants.RECEIVER_ID
import com.example.helloworld.common.Constants.RECEIVER_IMAGE
import com.example.helloworld.common.Constants.RECEIVER_NAME
import com.example.helloworld.common.Constants.RESULT_KEY
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

    private val notificationId = Random.nextInt(1000)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.S)
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

            createNormalNotification(title!!, message!!,hisId!!,chatUid!!, hisImage!!, pendingIntent)
        }
    }

    private fun updateToken(token: String) {
        val databaseReference = firebaseDatabase.child(USERS).child(firebaseAuth.uid!!)
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun createNormalNotification(title: String , message: String , receiverId: String , chatId: String ,image : String, pendingIntent: PendingIntent) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        }else 0

        val remoteInput = RemoteInput.Builder(RESULT_KEY).run {
            setLabel("Reply")
            build()
        }

        val replyIntent = Intent(this, NotificationReceiver::class.java).apply {
          //  action = "REPLY_ACTION"
            putExtra(RECEIVER_ID, receiverId)
            putExtra(CHAT_ID, chatId)
            putExtra(RECEIVER_NAME, title)
            putExtra(RECEIVER_IMAGE, image)
            putExtra("NOTIFICATION_ID", notificationId)
        }

        val replyPendingIntent = PendingIntent.getBroadcast(this,1,replyIntent, flag )

        val person = Person.Builder().setName(title).build()
        val notificationStyle = NotificationCompat.MessagingStyle(person)
            .addMessage(message, System.currentTimeMillis(),person)

        val replyAction = NotificationCompat.Action.Builder(0,"Reply",replyPendingIntent)
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(uri)
            .setOnlyAlertOnce(true)
            .addAction(replyAction)

//        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        manager.notify(Random.nextInt(85 - 65), builder.build())

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }

    }

}