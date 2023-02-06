package com.example.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.result.Resource
import com.example.domain.use_case.user_use_case.CreateUserUseCase
import com.example.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(private val createUserUseCase: CreateUserUseCase) : ViewModel() {

    private val _createUserState = Channel<Resource<User>>(Channel.BUFFERED)
    val createUserState = _createUserState.receiveAsFlow()


    fun createUser(uid: String, name: String) {
        createUserUseCase(uid, name).onEach { result ->
            when(result) {
                is  Resource.Loading -> {
                    _createUserState.send(result)
                }

                is Resource.Success -> {
                    _createUserState.send(result)
                }

                is Resource.Error -> {
                    _createUserState.send(result)
                }
            }
        }.launchIn(viewModelScope)
    }

}