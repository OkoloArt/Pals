package com.example.network.model.friends

import com.squareup.moshi.Json

data class FriendsData (
    @Json(name = "uid")
    val uid: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "conversationId")
    val conversationId: String?,
    )