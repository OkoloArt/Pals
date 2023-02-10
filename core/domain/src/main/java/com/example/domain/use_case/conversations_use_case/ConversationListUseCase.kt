package com.example.domain.use_case.conversations_use_case

import com.example.common.result.Resource
import com.example.data.repository.conversation_repository.ConversationRepository
import com.example.model.Conversations
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationListUseCase @Inject constructor( private val conversationRepository: ConversationRepository) {

    operator fun invoke() : Flow<Resource<List<Conversations>>>
    {
        return conversationRepository.getConversations()
    }
}