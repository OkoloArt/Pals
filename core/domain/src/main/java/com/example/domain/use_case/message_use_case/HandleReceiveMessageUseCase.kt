package com.example.domain.use_case.message_use_case

import com.example.common.result.Resource
import com.example.data.repository.message_repository.MessageRepository
import javax.inject.Inject
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import com.tinder.scarlet.Message as MessageScarlet

class HandleReceiveMessageUseCase @Inject constructor(private val messageRepository: MessageRepository) {

    operator fun invoke (response : WebSocket.Event) : MessageScarlet?{
        return messageRepository.onReceiveResponseConnection(response)
    }
}