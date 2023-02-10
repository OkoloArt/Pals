package com.example.network.model.conversations

import com.squareup.moshi.Json

data class ConversationData(
    @Json(name = "conversationId")
    val conversationId: String?,
    @Json(name = "unreadMessageCount")
    val unreadMessageCount: String?,
    @Json(name = "lastMessage")
    val lastMessage: LastMessage?,
)

data class LastMessage(
    @Json(name = "data")
    val data: Data?,
)

data class Data(
    @Json(name = "text")
    val text: String?,
)