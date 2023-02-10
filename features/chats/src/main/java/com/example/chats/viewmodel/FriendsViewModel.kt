package com.example.chats.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.result.Resource
import com.example.domain.use_case.friends_use_case.FriendListUseCase
import com.example.model.Friends
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(private val friendListUseCase: FriendListUseCase ) : ViewModel() {

    private var _friendsList = MutableLiveData<Resource<List<Friends>>>()
    val friendsList: LiveData<Resource<List<Friends>>> get() = _friendsList

    init {
        getFriendsList("09121338526")
    }

    private fun getFriendsList(uid : String){
        friendListUseCase(uid).onEach { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _friendsList.value = result
                }
                is Resource.Error -> {}
            }
        }.launchIn(viewModelScope)
    }
}