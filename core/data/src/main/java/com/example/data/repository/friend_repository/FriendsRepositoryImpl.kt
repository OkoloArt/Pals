package com.example.data.repository.friend_repository

import com.example.common.result.Resource
import com.example.data.model.toFriends
import com.example.model.Friends
import com.example.network.retrofit.HelloWorldApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi) :
    FriendsRepository
{

    override fun getFriendsList(uid: String): Flow<Resource<List<Friends>>> = flow {

        try
        {
            emit(Resource.Loading())

            val friendsList = helloWorldApi.getFriendsList(uid).data.map { it.toFriends() }
            emit(Resource.Success(friendsList))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }

    }
}