package com.example.data.model

import com.example.model.Conversations
import com.example.model.Friends
import com.example.model.Messages
import com.example.model.User
import com.example.network.model.conversations.ConversationData
import com.example.network.model.friends.FriendsData
import com.example.network.model.messages.MessageData
import com.example.network.model.messages.SendMessageResponse
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

fun SendMessageResponse.toMessage() : Messages{
    return Messages(
            message = this.sendMessageData.data?.text,
            isFromSender = null,
            action = false
    )
}

fun ConversationData.toConversations(): Conversations {
    return Conversations(
            conversationId ,
            unreadMessageCount ,
            lastMessage = this.lastMessage?.data?.text,
            receiver_name = this.conversationWith?.name,
            receiver_image = this.conversationWith?.avatar,
            status = this.conversationWith?.status
    )
}

//fun MessageData.toMessages() : Message{
//    return when (this.sender) {
//        "09121338526" -> {
//            Message(
//                    receiverMessage = "",
//                    senderMessage  = this.data?.text
//            )
//        }
//        else -> {
//            Message(
//                    receiverMessage = this.data?.text,
//                    senderMessage = ""
//            )
//        }
//    }
//}

fun MessageData.toMessages() : Messages{
    return Messages(
            message = this.data?.text,
            isFromSender = this.sender == "09121338526",
            action = this.data?.action == "deleted"
    )
}
