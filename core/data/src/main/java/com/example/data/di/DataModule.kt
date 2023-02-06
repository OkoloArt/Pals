package com.example.data.di

import com.example.data.friend_repository.FriendsRepository
import com.example.data.friend_repository.FriendsRepositoryImpl
import com.example.data.user_repository.UserRepository
import com.example.data.user_repository.UserRepositoryImpl
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
}