package com.example.helloworld.common.services

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDeepLinkBuilder
import com.example.helloworld.R
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.Constants.CHAT_ID
import com.example.helloworld.common.Constants.RECEIVER_ID
import com.example.helloworld.common.Constants.RECEIVER_IMAGE
import com.example.helloworld.common.Constants.RECEIVER_NAME
import com.example.helloworld.common.Constants.RESULT_KEY
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.GetCurrentUseCase
import com.example.helloworld.domain.use_cases.SendMessageUseCase
import com.example.helloworld.ui.fragments.MessageFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sendMessageUseCase: SendMessageUseCase
    @Inject
    lateinit var getCurrentUseCase: GetCurrentUseCase
    @Inject
    lateinit var userPreferences: UserPreferences

    private val receiver = User()

    override fun onReceive(context: Context?, intent: Intent?) {
            val remoteInput = intent?.let { RemoteInput.getResultsFromIntent(it) }
            if (remoteInput != null) {
                val message = remoteInput.getCharSequence(RESULT_KEY)?.toString()
                val receiverId = intent.getStringExtra(RECEIVER_ID)
                val chatUid = intent.getStringExtra(CHAT_ID)
                val receiverName = intent.getStringExtra(RECEIVER_NAME)
                val receiverImage = intent.getStringExtra(RECEIVER_IMAGE)
                if (message != null && receiverId != null && chatUid != null) {
                    receiver.apply {
                        username = receiverName
                        chatId = chatUid
                        userId = receiverId
                        image = receiverImage
                    }

                    val args = MessageFragmentArgs(receiver).toBundle()

                    val pendingIntent = NavDeepLinkBuilder(context!!)
                        .setGraph(R.navigation.nav_graph)
                        .setArguments(args)
                        .setDestination(R.id.messageFragment)
                        .createPendingIntent()

                    sendMessageUseCase(message , receiverId , chatUid)
                    getCurrentUseCase { sender ->
                        // Update the notification with the message
                        val notificationManager = NotificationManagerCompat.from(context!!)
                        val notificationId = intent.getIntExtra("NOTIFICATION_ID" , 0)
                        val notification = createNotification(context , message , sender.username!! , pendingIntent)
                        notificationManager.notify(notificationId , notification)
                    }
                }

            }
    }

    private fun createNotification(context: Context , message: String , sender: String, pendingIntent: PendingIntent): Notification {
        // Create the notification with the updated message
        // Use the receiverId and chatId to update the appropriate notification
        return NotificationCompat.Builder(context , CHANNEL_ID)
            .setContentTitle(sender)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}