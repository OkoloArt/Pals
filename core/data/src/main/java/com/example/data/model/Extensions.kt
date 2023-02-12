package com.example.data.model

import com.example.model.Conversations
import com.example.model.Friends
import com.example.model.User
import com.example.network.model.conversations.ConversationData
import com.example.network.model.friends.FriendsData
import com.example.network.model.user.UserResponse

fun UserResponse.toCreateUser(): User {
    return User(
            uid = this.userData.uid ,
            name = this.userData.name ,
            status = this.userData.status ,
            role = this.userData.role ,
            createdAt = this.userData.createdAt ,
            authToken = this.userData.authToken
    )
}

fun FriendsData.toFriends(): Friends {
    return Friends(
            uid ,
            name ,
            status ,
            avatar ,
            conversationId
    )
}

fun ConversationData.toConversations(): Conversations {
    return Conversations(
            conversationId ,
            unreadMessageCount ,
            lastMessage = this.lastMessage?.data?.text,
            receiver_name = this.conversationWith?.name,
            receiver_image = this.conversationWith?.avatar
    )
}