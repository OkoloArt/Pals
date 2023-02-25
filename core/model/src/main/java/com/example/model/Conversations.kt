package com.example.model

import java.io.Serializable

data class Conversations(
    val conversationId: String? ,
    val unreadMessageCount: String? ,
    val lastMessage: String? ,
    val receiver_name: String? ,
    val receiver_image: String? ,
    val status: String?,
) : Serializable