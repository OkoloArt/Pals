package com.example.network.model.user

import com.squareup.moshi.Json

data class UserResponse(
    @Json(name = "code")
    val code: Int? ,
    @Json(name = "data")
    val userData: UserData ,
    @Json(name = "message")
    val message: String? ,
    @Json(name = "status")
    val status: String? ,
)