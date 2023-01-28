package com.example.network.retrofit

import com.example.network.model.user.CreateUserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HelloWorldApi {
    @FormUrlEncoded
    @POST("users")
    suspend fun createUser(
        @Field("uid") uid: String ,
        @Field("name") name: String ,
    ): CreateUserResponse
}