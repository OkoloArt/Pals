package com.example.data.repository.message_repository

import com.example.common.result.Resource
import com.example.data.model.toMessage
import com.example.data.model.toMessages
import com.example.model.Messages
import com.example.network.model.messages.MessageDataObject
import com.example.network.retrofit.HelloWorldApi
import com.example.network.retrofit.SocketService
import com.google.gson.Gson
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.WebSocket.Event.*
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.tinder.scarlet.Message as MessageScarlet

class MessageRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi, private val socketService: SocketService) : MessageRepository {

    override fun getMessages(uid: String): Flow<Resource<List<Messages>>> = flow {
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

    override fun sendMessage(receiver: String , receiverType: String , category: String , type: String , text: String): Flow<Resource<Messages>>  = flow {
        try {

            val exampleMessage = MessageDataObject(text)

            val gson = Gson()
            val body = MultipartBody.Part.createFormData(
                    "data", null, gson.toJson(exampleMessage).toRequestBody("application/json".toMediaTypeOrNull())
            )

            val receiverPart = MultipartBody.Part.createFormData(name = "receiver", value = receiver)

            val categoryPart = MultipartBody.Part.createFormData(name = "category", value = category)

            val typePart = MultipartBody.Part.createFormData(name = "type", value = type)

            val receiverTypePart = MultipartBody.Part.createFormData(name = "receiverType", value = receiverType)

            emit(Resource.Loading())
            val message = helloWorldApi.sendMessage(receiverPart, receiverTypePart, categoryPart, typePart, body).toMessage()
            socketService.sendMessage(message.message!!)
            emit(Resource.Success(message))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }

    override fun observeConnection() : Flow<Resource<WebSocket.Event>>  = flow{
        socketService.observeConnection()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response ->
                runBlocking{ emit(Resource.Success(response)) }
            }
    }

     override fun onReceiveResponseConnection(response: WebSocket.Event) : MessageScarlet? {
        return when (response) {
            is OnConnectionOpened<*> -> { null }
            is OnConnectionClosed -> { null }
            is OnConnectionClosing -> { null }
            is OnConnectionFailed -> { null }
            is OnMessageReceived -> {response.message}
        }
    }
}