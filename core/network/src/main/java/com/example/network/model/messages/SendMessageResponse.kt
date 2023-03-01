package com.example.network.model.messages

import com.squareup.moshi.Json

data class SendMessageResponse (
        @Json(name = "data")
        val sendMessageData : SendMessageData
)