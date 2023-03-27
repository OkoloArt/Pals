package com.example.helloworld.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.helloworld.data.model.User
import com.example.helloworld.domain.use_cases.GetContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val getContactUseCase: GetContactUseCase) : ViewModel(){

    fun getAppContact(user: User,mobileContacts: List<User>, callback: (ArrayList<User>) -> Unit) = getContactUseCase(user, mobileContacts, callback)

}
