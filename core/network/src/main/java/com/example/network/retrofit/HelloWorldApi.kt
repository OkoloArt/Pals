package com.example.network.retrofit

import com.example.network.model.conversations.ConversationResponse
import com.example.network.model.friends.FriendResponse
import com.example.network.model.messages.MessageDataObject
import com.example.network.model.messages.MessageResponse
import com.example.network.model.messages.Receiver
import com.example.network.model.messages.SendMessageResponse
import com.example.network.model.user.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.*

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

    @Multipart
    @POST("messages")
    suspend fun sendMessage(
        @Part receiver: MultipartBody.Part ,
        @Part receiverType: MultipartBody.Part ,
        @Part category: MultipartBody.Part ,
        @Part type: MultipartBody.Part ,
        @Part dataObject: MultipartBody.Part
    ): SendMessageResponse
}