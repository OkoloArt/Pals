package com.example.helloworld.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.CheckChatUseCase
import com.example.helloworld.domain.use_cases.CreateChatUseCase
import com.example.helloworld.domain.use_cases.ReceiverUseCase
import com.example.helloworld.domain.use_cases.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val checkChatUseCase: CheckChatUseCase,
private val createChatUseCase: CreateChatUseCase, private val sendMessageUseCase: SendMessageUseCase, private val receiverUseCase: ReceiverUseCase) : ViewModel() {

    fun checkChat(receiverId: String , callback: (String) -> Unit) = checkChatUseCase(receiverId , callback)

    fun createChat(lastMessage: String, receiverId: String) = createChatUseCase(lastMessage, receiverId)

    fun sendMessage(message: String, receiverId: String,chatUid : String?) = sendMessageUseCase(message, receiverId,chatUid)

    fun getUser(member: String , callback : (User) -> Unit) = receiverUseCase(member , callback)
}