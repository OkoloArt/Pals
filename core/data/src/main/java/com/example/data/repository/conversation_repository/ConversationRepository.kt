package com.example.data.repository.conversation_repository

import com.example.common.result.Resource
import com.example.model.Conversations
import com.example.model.Friends
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(): Flow<Resource<List<Conversations>>>
}