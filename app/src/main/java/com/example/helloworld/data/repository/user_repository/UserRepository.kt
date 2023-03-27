package com.example.helloworld.data.repository.user_repository

import androidx.lifecycle.LiveData
import com.example.helloworld.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface UserRepository {

    fun createUser(email : String, password : String): Task<AuthResult>

    fun uploadData(user: User)

    fun updateUser(user: User)

    fun getUser() : LiveData<User>

    fun getAppContacts(user: User, mobileContacts: List<User>, callback: (ArrayList<User>) -> Unit)
}