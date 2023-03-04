package com.example.network.retrofit

import com.example.model.Messages
import com.example.network.model.messages.MessageData
import com.example.network.model.messages.MessageDataObject
import com.example.network.model.messages.MessageResponse
import com.squareup.moshi.Json
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable
import kotlinx.coroutines.channels.ReceiveChannel

interface SocketService {
    @Receive
    fun observeConnection(): Flowable<WebSocket.Event>

    @Send
    fun subscribe(action: MessageResponse)



}


