package com.example.network.model.conversations

import com.example.network.model.user.UserData
import com.squareup.moshi.Json

data class ConversationResponse(
    @Json(name = "data")
    val conversationData: List<ConversationData>,
)