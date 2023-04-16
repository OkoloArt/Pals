package com.example.helloworld.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val checkChatUseCase: CheckChatUseCase ,
    private val sendMessageUseCase: SendMessageUseCase ,
    private val receiverUseCase: ReceiverUseCase ,
    private val getTokenUseCase: GetTokenUseCase,
    private val getCurrentUseCase: GetCurrentUseCase
) : ViewModel() {

    fun checkChat(receiverId: String , callback: (String) -> Unit) = checkChatUseCase(receiverId , callback)

    fun sendMessage(message: String, receiverId: String,chatUid : String?) = sendMessageUseCase(message, receiverId,chatUid)

    fun getUser(member: String , callback : (User) -> Unit) = receiverUseCase(member , callback)

    fun getToken(message: String, receiver: User, sender : User, chatUid: String, context: Context) = getTokenUseCase(message, receiver, sender, chatUid, context)

    fun getCurrentUser(callback: (User) -> Unit) = getCurrentUseCase(callback)
}