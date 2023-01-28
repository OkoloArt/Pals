package com.example.domain.use_case.create_user_use_case

import com.example.common.result.Resource
import com.example.data.create_user_repository.CreateUserRepository
import com.example.model.CreateUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(private val createUserRepository: CreateUserRepository) {
    operator fun invoke(uid: String , name: String): Flow<Resource<CreateUser>> {
        return createUserRepository.createUser(uid , name)
    }
}