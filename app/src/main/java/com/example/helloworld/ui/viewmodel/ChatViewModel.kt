package com.example.helloworld.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.Chat
import com.example.helloworld.data.model.ChatList
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.ChatUseCase
import com.example.helloworld.domain.use_cases.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatUseCase: ChatUseCase , private val getUserUseCase: GetUserUseCase ): ViewModel() {

    fun getChats(chatList: ChatList , callback : (Chat) -> Unit) = chatUseCase(chatList, callback)

    fun getUser(chatList: ChatList , callback : (User) -> Unit) = getUserUseCase(chatList, callback)
}