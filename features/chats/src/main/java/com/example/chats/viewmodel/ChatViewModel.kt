package com.example.chats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.result.Resource
import com.example.domain.use_case.conversations_use_case.ConversationListUseCase
import com.example.model.Conversations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(private val conversationListUseCase: ConversationListUseCase) : ViewModel() {

    private var _conversationList = MutableLiveData<Resource<List<Conversations>>>()
    val conversationList: LiveData<Resource<List<Conversations>>> get() = _conversationList

    init {
        getConversation()
    }

    private fun getConversation(){
        conversationListUseCase().onEach { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _conversationList.value = result
                }
                is Resource.Error -> {}
            }
        }.launchIn(viewModelScope)
    }
}