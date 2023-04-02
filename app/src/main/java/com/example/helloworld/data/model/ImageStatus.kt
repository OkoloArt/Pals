package com.example.helloworld.data.model

data class UserStatus(var name:String, var status: List<ImageStatus>)

data class ImageStatus(var image: String , var date : String)
