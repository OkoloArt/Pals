package com.example.domain.use_case.friends_use_case

import com.example.common.result.Resource
import com.example.data.friend_repository.FriendsRepository
import com.example.model.Friends
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FriendListUseCase @Inject constructor(private val friendsRepository: FriendsRepository) {

    operator fun invoke(uid : String) : Flow<Resource<List<Friends>>> {
        return friendsRepository.getFriendsList(uid)
    }
}