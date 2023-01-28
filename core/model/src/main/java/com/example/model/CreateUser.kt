package com.example.model

data class CreateUser(
    val uid: String? ,
    val name: String? ,
    val status: String? ,
    val role: String? ,
    val createdAt: Int? ,
    val authToken: String? ,
)