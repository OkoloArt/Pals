package com.example.helloworld.data.model

data class Message(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = "",
    var date: String = "",
    var type: String = ""
)