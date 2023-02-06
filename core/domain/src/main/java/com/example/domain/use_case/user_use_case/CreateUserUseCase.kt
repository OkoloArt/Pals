package com.example.domain.use_case.user_use_case

import com.example.common.result.Resource
import com.example.data.user_repository.UserRepository
import com.example.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(uid: String , name: String): Flow<Resource<User>> {
        return userRepository.createUser(uid , name)
    }
}