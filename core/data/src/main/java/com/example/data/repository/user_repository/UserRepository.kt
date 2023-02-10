package com.example.data.repository.user_repository

import com.example.common.result.Resource
import com.example.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun createUser(uid : String, name : String) : Flow<Resource<User>>
}