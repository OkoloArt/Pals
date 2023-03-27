package com.example.helloworld.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.ProfileUseCase
import com.example.helloworld.domain.use_cases.UpdateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val profileUseCase: ProfileUseCase,
private val updateUserUseCase: UpdateUserUseCase) : ViewModel(){

//    private val _user = MutableLiveData<User?>()
//    val user: LiveData<User?> get() = _user

    fun getUser() : LiveData<User> = profileUseCase()

    fun updateUser(user: User) = updateUserUseCase(user)
}