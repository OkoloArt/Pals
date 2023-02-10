package com.example.data.repository.conversation_repository

import com.example.common.result.Resource
import com.example.data.model.toConversations
import com.example.data.model.toFriends
import com.example.model.Conversations
import com.example.network.retrofit.HelloWorldApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(private val helloWorldApi: HelloWorldApi) : ConversationRepository{

    override fun getConversations(): Flow<Resource<List<Conversations>>> = flow {
        try {
            emit(Resource.Loading())

            val conversationList = helloWorldApi.getConversation().conversationData.map { it.toConversations() }
            emit(Resource.Success(conversationList))

        }catch (e: HttpException) {
            emit(Resource.Error( "An unexpected Error Occurred kindly check your login detail"))
        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't reach server please check your internet connection"))
        }
    }
}