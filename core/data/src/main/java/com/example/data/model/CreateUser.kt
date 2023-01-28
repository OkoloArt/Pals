package com.example.data.model

import com.example.model.CreateUser
import com.example.network.model.user.CreateUserResponse

fun CreateUserResponse.toCreateUser() : CreateUser{
    return CreateUser(
            uid = this.data.uid,
            name = this.data.name,
            status = this.data.status,
            role = this.data.role,
            createdAt = this.data.createdAt,
            authToken = this.data.authToken
    )
}