package com.example.helloworld.di

import com.example.helloworld.data.repository.chat_repository.ChatRepository
import com.example.helloworld.data.repository.chat_repository.ChatRepositoryImpl
import com.example.helloworld.data.repository.message_repository.MessageRepository
import com.example.helloworld.data.repository.message_repository.MessageRepositoryImpl
import com.example.helloworld.data.repository.user_repository.UserRepository
import com.example.helloworld.data.repository.user_repository.UserRepositoryImpl
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
    fun messageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @Binds
    fun chatRepository(chatRepostoryImpl: ChatRepositoryImpl): ChatRepository


}