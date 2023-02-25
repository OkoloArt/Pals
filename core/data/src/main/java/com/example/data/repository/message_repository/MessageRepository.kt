package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getMessages(uid : String) : Flow<Resource<List<Message>>>
}