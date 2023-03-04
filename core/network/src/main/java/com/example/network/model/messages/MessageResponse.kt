package com.example.network.model.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class MessageResponse(
    @Json(name = "data")
     val messageData: List<MessageData>? = emptyList() ,
 )