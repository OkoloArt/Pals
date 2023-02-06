package com.example.network.model.friends

import com.example.network.model.friends.Data
import com.squareup.moshi.Json

data class FriendResponse(
    @Json(name = "code")
    val code: Int? ,
    @Json(name = "data")
    val data: List<Data> ,
    @Json(name = "message")
    val message: String? ,
    @Json(name = "status")
    val status: String? ,
)
