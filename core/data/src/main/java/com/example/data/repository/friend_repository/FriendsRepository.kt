package com.example.data.repository.friend_repository

import com.example.common.result.Resource
import com.example.model.Friends
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {

    fun getFriendsList(uid : String): Flow<Resource<List<Friends>>>
}