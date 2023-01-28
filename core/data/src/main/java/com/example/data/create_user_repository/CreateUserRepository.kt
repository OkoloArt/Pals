package com.example.data.create_user_repository

import com.example.common.result.Resource
import com.example.model.CreateUser
import kotlinx.coroutines.flow.Flow

interface CreateUserRepository {
    fun createUser(uid : String, name : String) : Flow<Resource<CreateUser>>
}