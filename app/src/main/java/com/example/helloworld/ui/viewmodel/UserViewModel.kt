package com.example.helloworld.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.UploadDataUsecase
import com.example.helloworld.domain.use_cases.createUserUsecase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val createUserUsecase: createUserUsecase ,
    private val uploadDataUsecase: UploadDataUsecase ,
) : ViewModel() {

    fun createUser(email: String, password: String) : Task<AuthResult> = createUserUsecase(email , password)

    fun uploadData(user: User) = uploadDataUsecase(user)

}