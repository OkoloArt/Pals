package com.example.network.retrofit

import com.example.network.model.messages.Data
import com.example.network.model.messages.MessageData
import com.example.network.model.messages.MessageResponse
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface SocketService {
    @Receive
    fun observeConnection(): Flowable<WebSocket.Event>

    @Send
    fun subscribe(action: MessageResponse)

    @Receive
    fun observeTicker(): Flowable<Data>

}

