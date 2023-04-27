package com.example.helloworld.common.services

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.helloworld.R
import com.example.helloworld.common.Constants.CHANNEL_ID
import com.example.helloworld.common.Constants.CHAT_ID
import com.example.helloworld.common.Constants.RECEIVER_ID
import com.example.helloworld.common.Constants.RESULT_KEY
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.GetCurrentUseCase
import com.example.helloworld.domain.use_cases.GetTokenUseCase
import com.example.helloworld.domain.use_cases.SendMessageUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sendMessageUseCase: SendMessageUseCase
    @Inject
    lateinit var getTokenUseCase: GetTokenUseCase
    @Inject
    lateinit var getCurrentUseCase: GetCurrentUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null && action == "REPLY_ACTION")
            {
                val message = remoteInput.getCharSequence(RESULT_KEY)?.toString()
                val receiverId = intent.getStringExtra(RECEIVER_ID)
                val chatId = intent.getStringExtra(CHAT_ID)
                if (message != null && receiverId != null && chatId != null) {
                    sendMessageUseCase(message , receiverId , chatId)
                    getCurrentUseCase { sender ->
                        if (context != null) {
                            getTokenUseCase(message , receiver = User(userId = receiverId), sender,chatId,context)
                        }
                        // Update the notification with the message
                        val notificationManager = NotificationManagerCompat.from(context!!)
                        val notificationId = intent.getIntExtra("NOTIFICATION_ID" , 0)
                        val notification = createNotification(context , message , sender.username!!)
                        notificationManager.notify(notificationId , notification)
                    }
                }

            }
        }
}

    private fun createNotification(context: Context , message: String , sender: String): Notification {
        // Create the notification with the updated message
        // Use the receiverId and chatId to update the appropriate notification
        return NotificationCompat.Builder(context , CHANNEL_ID)
            .setContentTitle(sender)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }
}