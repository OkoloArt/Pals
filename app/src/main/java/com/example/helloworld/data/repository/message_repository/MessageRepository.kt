package com.example.helloworld.data.repository.message_repository

import com.example.helloworld.data.model.User

interface MessageRepository {

    fun checkChat(receiverId: String, callback: (String) -> Unit)

    fun createChat(lastMessage: String, receiverId: String)

    fun sendMessage(message: String, receiverId: String, chatId : String?)

    fun getUser(member: String , callback: (User) -> Unit)
}