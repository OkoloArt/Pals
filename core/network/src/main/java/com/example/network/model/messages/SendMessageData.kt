package com.example.network.model.messages

import com.squareup.moshi.Json

data class SendMessageData(
    @Json(name = "id")
    val id: String?,
    @Json(name = "data")
    val data: SendData?,
)

data class SendData(
    @Json(name = "text")
    val text: String?,
)

data class MessageDataObject(
    @Json(name = "text")
    val text: String?
)