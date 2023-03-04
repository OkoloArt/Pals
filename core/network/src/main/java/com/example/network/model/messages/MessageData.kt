package com.example.network.model.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageData(
    @Json(name = "conversationId")
    val conversationId: String?,
    @Json(name = "sender")
    val sender: String?,
    @Json(name = "receiver")
    val receiver: String?,
    @Json(name = "data")
    val data: Data?,
)

data class Data(
    @Json(name = "text")
    val text: String?,
    @Json(name = "action")
    val action: String?,
    @Json(name = "entities")
    val entities: Entities?,
)

data class Entities(
    @Json(name = "receiver")
    val text: Receiver?,
)

data class Receiver (
    @Json(name = "entity")
    val text: Entity?,
)

data class Entity (
    @Json(name = "uid")
    val uid: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "status")
    val status: String?,
)
