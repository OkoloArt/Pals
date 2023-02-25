package com.example.chats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.result.Resource
import com.example.domain.use_case.message_use_case.MessageUseCase
import com.example.model.Friends
import com.example.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val messageUseCase: MessageUseCase) : ViewModel() {

    private var _messages = MutableLiveData<Resource<List<Message>>>()
    val messages: LiveData<Resource<List<Message>>> get() = _messages

    init {
        getMessages("superhero4")
    }

    private fun getMessages(uid : String){
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
}