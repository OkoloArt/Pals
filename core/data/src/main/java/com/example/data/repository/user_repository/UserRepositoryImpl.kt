package com.example.data.repository.user_repository

import com.example.common.result.Resource
import com.example.data.model.toCreateUser
import com.example.model.User
import com.example.network.retrofit.HelloWorldApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi) :
    UserRepository
{

    override fun createUser(uid: String , name: String): Flow<Resource<User>> = flow {

        try {
            emit(Resource.Loading())

            val createUser = helloWorldApi.createUser(uid, name).toCreateUser()
            emit(Resource.Success(data = createUser))

        } catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }
}