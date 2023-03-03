package com.example.network.retrofit

import com.example.network.model.messages.SendMessageResponse
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable
import com.example.model.Messages
import okhttp3.MultipartBody
import retrofit2.http.Part
import com.tinder.scarlet.Message as MessageScarlet

interface SocketService {
    @Receive
    fun observeConnection(): Flowable<WebSocket.Event>

    @Send
    fun sendMessage(param: String)

}