package com.example.network.model.user

import com.squareup.moshi.Json

data class Data(
    @Json(name = "uid")
    val uid: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "role")
    val role: String?,
    @Json(name = "createdAt")
    val createdAt: Int?,
    @Json(name = "authToken")
    val authToken: String?,
)
