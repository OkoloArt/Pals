package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.model.Messages
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import com.tinder.scarlet.Message as MessageScarlet


interface MessageRepository {

    fun getMessages(uid : String) : Flow<Resource<List<Messages>>>

    fun sendMessage(receiver: String ,receiverType: String, category: String, type: String , text: String) : Flow<Resource<Messages>>

    fun observeConnection() :  Flow<Resource<WebSocket.Event>>

    fun onReceiveResponseConnection(response: WebSocket.Event) : MessageScarlet?
}