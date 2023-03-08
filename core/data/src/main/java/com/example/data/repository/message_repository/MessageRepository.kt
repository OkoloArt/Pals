package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.model.Messages
import kotlinx.coroutines.flow.Flow


interface MessageRepository {

    fun getMessages(uid : String) : Flow<Resource<List<Messages>>>

    fun sendMessage(receiver: String ,receiverType: String, category: String, type: String , text: String) : Flow<Resource<Messages>>

    fun getMessage(uid: String): Flow<Resource<List<Messages>>>

    fun observeTicker() : Flow<Resource<String>>

    fun observeConnection(uid: String)
}