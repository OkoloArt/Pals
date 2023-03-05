package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.data.model.toMessage
import com.example.data.model.toMessages
import com.example.model.Messages
import com.example.network.model.messages.MessageDataObject
import com.example.network.retrofit.HelloWorldApi
import com.example.network.retrofit.SocketService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class MessageRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi, private val socketService: SocketService) : MessageRepository {

    override fun getMessages(uid: String): Flow<Resource<List<Messages>>> = flow {
        try {
            emit(Resource.Loading())
            val message = helloWorldApi.getMessages(uid).messageData!!.map { it.toMessages() }
            emit(Resource.Success(message))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }

    override fun sendMessage(receiver: String , receiverType: String , category: String , type: String , text: String): Flow<Resource<Messages>>  = flow {
        try {

            val exampleMessage = MessageDataObject(text)

            val gson = Gson()
            val body = MultipartBody.Part.createFormData(
                    "data", null, gson.toJson(exampleMessage).toRequestBody("application/json".toMediaTypeOrNull())
            )

            val receiverPart = MultipartBody.Part.createFormData(name = "receiver" , value = receiver)

            val categoryPart = MultipartBody.Part.createFormData(name = "category", value = category)

            val typePart = MultipartBody.Part.createFormData(name = "type", value = type)

            val receiverTypePart = MultipartBody.Part.createFormData(name = "receiverType", value = receiverType)

            emit(Resource.Loading())
            val message = helloWorldApi.sendMessage(receiverPart, receiverTypePart, categoryPart, typePart, body).toMessage()
            emit(Resource.Success(message))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }

    override fun observeTicker(): Flow<Resource<Messages>> = flow {
        try {
            socketService.observeTicker()
                .subscribe { messages ->
                    runBlocking {
                        emit(Resource.Success(Messages(messages.text)))
                    }
                }
        }catch (e: HttpException) {
            emit( Resource.Error(""))
        }
    }

    override fun observeConnection(uid: String) {
       runBlocking{
            socketService.observeConnection()
                .subscribe {
                    runBlocking {
                        val messages = helloWorldApi.getMessages(uid)
                        socketService.subscribe(messages) }
                }
        }
    }
}