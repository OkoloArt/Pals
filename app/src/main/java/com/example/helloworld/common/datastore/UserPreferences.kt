package com.example.helloworld.common.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferences {

    val uid: Flow<String?>
    suspend fun saveUid(uid: String)

    val token: Flow<String?>
    suspend fun saveToken(token: String)
}