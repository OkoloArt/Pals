package com.example.helloworld.domain.use_cases

import androidx.lifecycle.LiveData
import com.example.helloworld.data.model.User
import com.example.helloworld.data.repository.user_repository.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import javax.inject.Inject
class createUserUsecase @Inject constructor(private val userRepository: UserRepository){

    operator fun invoke(email:String, password:String): Task<AuthResult>
    {
        return userRepository.createUser(email, password)
    }
}

class UploadDataUsecase @Inject constructor(private val userRepository: UserRepository){

    operator fun invoke(user: User) {
        return userRepository.uploadData(user)
    }
}

class UpdateUserUseCase @Inject constructor(private val userRepository: UserRepository){

    operator fun invoke(user: User) = userRepository.updateUser(user)
}

class ProfileUseCase @Inject constructor(private val userRepository: UserRepository){

    operator fun invoke(): LiveData<User> = userRepository.getUser()
}
