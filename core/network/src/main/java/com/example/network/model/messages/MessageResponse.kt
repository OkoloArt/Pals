package com.example.network.model.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageResponse(
    @Json(name = "type") val type: String = "subscribe",
    @Json(name = "data")
     val messageData: List<MessageData>? = emptyList() ,
 )