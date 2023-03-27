package com.example.helloworld.domain.use_cases

import com.example.helloworld.data.model.Chat
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.User
import com.example.helloworld.data.repository.chat_repository.ChatRepository
import javax.inject.Inject

class ChatUseCase @Inject constructor(private val chatRepository: ChatRepository){

    operator fun invoke(chatList: ChatList , callback : (Chat) -> Unit) = chatRepository.getChats(chatList, callback)
}

class GetUserUseCase @Inject constructor(private val chatRepository: ChatRepository){

    operator fun invoke(chatList: ChatList , callback : (User) -> Unit) = chatRepository.getUser(chatList, callback)
}
