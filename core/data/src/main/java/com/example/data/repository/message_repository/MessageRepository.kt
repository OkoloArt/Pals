package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.model.Message
import com.example.network.model.messages.MessageDataObject
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getMessages(uid : String) : Flow<Resource<List<Message>>>

    fun sendMessage(receiver: String ,receiverType: String, category: String, type: String , messageDataObject: MessageDataObject) : Flow<Resource<Message>>
}