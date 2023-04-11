package com.example.helloworld.data.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class User(
    @get: Exclude
    var userId: String? = null ,
    var email: String?  = null ,
    var username: String?  = null ,
    var chatId: String?  = null ,
    var online: String?  = null ,
    var typingStatus : Boolean? = false,
    val imageStatus : MutableList<ImageStatus>? = null,
    val status: String = "",
    val image: String = "",
    var number: String = "",
) : Serializable