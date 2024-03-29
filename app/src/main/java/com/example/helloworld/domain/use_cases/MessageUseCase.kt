package com.example.helloworld.domain.use_cases

import android.content.Context
import com.example.helloworld.data.model.User
import com.example.helloworld.data.repository.message_repository.MessageRepository
import javax.inject.Inject

class CheckChatUseCase @Inject constructor(private val messageRepository: MessageRepository){

    operator fun invoke (receiverId: String , callback: (String) -> Unit) = messageRepository.checkChat(receiverId, callback)
}

class CreateChatUseCase @Inject constructor(private val messageRepository: MessageRepository){

    operator fun invoke(lastMessage: String, receiverId: String) = messageRepository.createChat(lastMessage, receiverId)
}

class SendMessageUseCase @Inject constructor(private val messageRepository: MessageRepository){

    operator fun invoke(message: String, receiverId: String, chatUid : String?) = messageRepository.sendMessage(message, receiverId,chatUid)
}

class ReceiverUseCase @Inject constructor(private val messageRepository: MessageRepository){
    operator fun invoke(member : String ,callback: (User) -> Unit) = messageRepository.getUser(member, callback)
}

class GetTokenUseCase @Inject constructor(private val messageRepository: MessageRepository) {
    operator fun invoke(message: String , receiver: User ,
        sender: User , chatUid: String , context: Context , ) = messageRepository.getToken(message , receiver , sender , chatUid , context)
}

class GetCurrentUseCase @Inject constructor(private val messageRepository: MessageRepository){
    operator fun invoke(callback: (User) -> Unit)  = messageRepository.getCurrentUser(callback)
}