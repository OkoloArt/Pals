package com.example.domain.use_case.message_use_case

import com.example.common.result.Resource
import com.example.data.repository.message_repository.MessageRepository
import com.example.model.Messages
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase  @Inject constructor(private val messageRepository: MessageRepository) {

    operator fun invoke (receiver: String , receiverType: String , category: String , type: String , messageDataObject: String) : Flow<Resource<Messages>> {
        return messageRepository.sendMessage(receiver, receiverType, category, type, messageDataObject)
    }
}