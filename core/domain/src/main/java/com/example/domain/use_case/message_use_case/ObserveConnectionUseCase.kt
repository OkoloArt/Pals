package com.example.domain.use_case.message_use_case

import com.example.common.result.Resource
import com.example.data.repository.message_repository.MessageRepository
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectionUseCase @Inject constructor(private val messageRepository: MessageRepository)
{

    operator fun invoke() : Flow<Resource<WebSocket.Event>>{
        return messageRepository.observeConnection()
    }
}