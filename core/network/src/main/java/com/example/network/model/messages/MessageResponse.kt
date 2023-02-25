package com.example.network.model.messages

import com.squareup.moshi.Json

data class MessageResponse(
    @Json(name = "data")
     val messageData: List<MessageData> ,
 )