package com.example.helloworld.data.repository.chat_repository

import com.example.helloworld.data.model.Chat
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.User

interface ChatRepository {

    fun getChats(chatList: ChatList , callback : (Chat) -> Unit)

    fun getUser(chatList: ChatList , callback : (User) -> Unit , )
}