package com.example.chats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.result.Resource
import com.example.domain.use_case.message_use_case.MessageUseCase
import com.example.domain.use_case.message_use_case.SendMessageUseCase
import com.example.model.Messages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageUseCase: MessageUseCase ,
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel()
{

    private var _messages = MutableLiveData<Resource<List<Messages>>>()
    val messages: LiveData<Resource<List<Messages>>> get() = _messages


    private var _message = MutableLiveData<Resource<Messages>>()
    val message: LiveData<Resource<Messages>> get() = _message

    private lateinit var _receiver: String
    val receiver get() = _receiver

    private lateinit var _receiverType: String
    val receiverType get() = _receiverType

    private lateinit var _category: String
    val category get() = _category

    private lateinit var _type: String
    val type get() = _type

    private lateinit var _text: String
    val text get() = _text

    fun setValue(receiver: String , receiverType: String , category: String , type: String , text: String , ) {
        _receiver = receiver
        _receiverType = receiverType
        _category = category
        _type = type
        _text = text
    }

//    init {
//    //    getMessages("superhero1")
//      //  val response = observeConnection()
//
//    }

     fun getMessages(uid : String){
        messageUseCase(uid).onEach { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _messages.value = result
                }
                is Resource.Error -> {}
            }
        }.launchIn(viewModelScope)
    }

//    fun sendMessage(receiver: String , receiverType: String , category: String , type: String , text: String , ) {
//        sendMessageUseCase(receiver , receiverType , category , type , text).onEach { result ->
//            when (result) {
//                is Resource.Loading -> {}
//                is Resource.Success -> { _message.value = result }
//                is Resource.Error -> {}
//            }
//        }.launchIn(viewModelScope)
//    }

    fun sendMessage(): Flow<Resource<Messages>> = sendMessageUseCase(receiver , receiverType , category , type , text)

//    fun getMessages(): Flow<Resource<List<Message>>> = messageUseCase("superhero1")

}