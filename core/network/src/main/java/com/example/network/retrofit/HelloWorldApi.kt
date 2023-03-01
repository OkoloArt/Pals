package com.example.network.retrofit

import com.example.network.model.conversations.ConversationResponse
import com.example.network.model.friends.FriendResponse
import com.example.network.model.messages.MessageDataObject
import com.example.network.model.messages.MessageResponse
import com.example.network.model.messages.Receiver
import com.example.network.model.messages.SendMessageResponse
import com.example.network.model.user.UserResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HelloWorldApi {

    @FormUrlEncoded
    @POST("users")
    suspend fun createUser(
        @Field("uid") uid: String ,
        @Field("name") name: String ,
    ): UserResponse

    @GET("users/{uid}/friends")
    suspend fun getFriendsList(
        @Path("uid") uid: String
    ): FriendResponse

    @GET("conversations")
    suspend fun getConversation(): ConversationResponse

    @GET("users/{uid}/messages")
    suspend fun getMessages(
        @Path("uid") uid: String
    ): MessageResponse

    @FormUrlEncoded
    @POST("messages")
    suspend fun sendMessage(
        @Field("receiver") receiver: String ,
        @Field("receiverType") receiverType: String ,
        @Field("category") category: String ,
        @Field("type") type: String ,
        @Body messageDataObject: MessageDataObject
    ): SendMessageResponse
}