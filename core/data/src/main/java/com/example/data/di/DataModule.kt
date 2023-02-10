package com.example.data.di

import com.example.data.repository.conversation_repository.ConversationRepository
import com.example.data.repository.conversation_repository.ConversationRepositoryImpl
import com.example.data.repository.friend_repository.FriendsRepository
import com.example.data.repository.friend_repository.FriendsRepositoryImpl
import com.example.data.repository.user_repository.UserRepository
import com.example.data.repository.user_repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun userRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    fun friendsRepository(friendsRepositoryImpl: FriendsRepositoryImpl): FriendsRepository

    @Binds
    fun conversationRepository(conversationRepositoryImpl: ConversationRepositoryImpl) : ConversationRepository
}