package com.example.data.di

import com.example.data.create_user_repository.CreateUserRepository
import com.example.data.create_user_repository.CreateUserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun createUserRepository(createUserRepositoryImpl: CreateUserRepositoryImpl): CreateUserRepository
}