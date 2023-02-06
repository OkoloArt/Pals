package com.example.data.model

import com.example.model.Friends
import com.example.model.User
import com.example.network.model.friends.Data
import com.example.network.model.user.UserResponse

fun UserResponse.toCreateUser(): User
{
    return User(
            uid = this.data.uid ,
            name = this.data.name ,
            status = this.data.status ,
            role = this.data.role ,
            createdAt = this.data.createdAt ,
            authToken = this.data.authToken
    )
}

fun Data.toFriends(): Friends {
    return Friends(
            uid ,
            name ,
            status ,
            avatar ,
            conversationId
    )
}