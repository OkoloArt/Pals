package com.example.data.create_user_repository

import com.example.common.result.Resource
import com.example.data.model.toCreateUser
import com.example.model.CreateUser
import com.example.network.retrofit.HelloWorldApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CreateUserRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi) : CreateUserRepository{

    override fun createUser(uid: String , name: String): Flow<Resource<CreateUser>> = flow {

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