package com.example.helloworld.data.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class User(
    @get: Exclude
    var userId: String? = null ,
    var email: String?  = null ,
    var username: String?  = null ,
    @get: Exclude
    var chatId: String?  = null ,
    var online: String?  = null ,
    var typingStatus : Boolean? = false ,
    var imageStatus : MutableList<ImageStatus>? = null ,
    var status: String? = "" ,
    var image: String? = "" ,
    var number: String? = "" ,
    var token : String? = "",
) : Serializable