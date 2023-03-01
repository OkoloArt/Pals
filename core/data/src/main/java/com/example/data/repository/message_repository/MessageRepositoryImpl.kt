package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.data.model.toMessage
import com.example.data.model.toMessages
import com.example.model.Message
import com.example.network.model.messages.MessageDataObject
import com.example.network.retrofit.HelloWorldApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi) : MessageRepository {

    override fun getMessages(uid: String): Flow<Resource<List<Message>>> = flow {
        try {
            emit(Resource.Loading())
            val messages = helloWorldApi.getMessages(uid).messageData.map { it.toMessages() }
            emit(Resource.Success(messages))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }

    override fun sendMessage(receiver: String , receiverType: String , category: String , type: String , messageDataObject: MessageDataObject ,
    ): Flow<Resource<Message>>  = flow {
        try {
            emit(Resource.Loading())
            val message = helloWorldApi.sendMessage(receiver, receiverType, category, type, messageDataObject).toMessage()
            emit(Resource.Success(message))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }
}