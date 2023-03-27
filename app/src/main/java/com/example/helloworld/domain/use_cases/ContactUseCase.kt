package com.example.helloworld.domain.use_cases

import androidx.lifecycle.LiveData
import com.example.helloworld.data.model.User
import com.example.helloworld.data.repository.user_repository.UserRepository
import javax.inject.Inject

class GetContactUseCase @Inject constructor(private val userRepository: UserRepository){

    operator fun invoke (user: User,mobileContacts: List<User>,callback: (ArrayList<User>) -> Unit) = userRepository.getAppContacts(user, mobileContacts, callback)
}