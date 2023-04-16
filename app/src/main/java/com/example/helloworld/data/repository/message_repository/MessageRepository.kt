package com.example.helloworld.data.repository.message_repository

import android.content.Context
import com.example.helloworld.data.model.User
import org.json.JSONObject

interface MessageRepository {

    fun checkChat(receiverId: String, callback: (String) -> Unit)

    fun createChat(lastMessage: String, receiverId: String)

    fun sendMessage(message: String, receiverId: String, chatId : String?)

    fun getUser(member: String , callback: (User) -> Unit)

    fun getToken(message: String, receiver: User, sender : User, chatId: String, context: Context)

    fun sendNotification(token : JSONObject, context: Context)

    fun getCurrentUser(callback: (User) -> Unit)
}