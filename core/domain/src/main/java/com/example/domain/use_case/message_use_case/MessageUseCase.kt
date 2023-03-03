package com.example.domain.use_case.message_use_case

import com.example.common.result.Resource
import com.example.data.repository.message_repository.MessageRepository
import com.example.model.Messages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    operator fun invoke (uid : String) : Flow<Resource<List<Messages>>> {
        return messageRepository.getMessages(uid)
    }
}