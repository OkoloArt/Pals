package com.example.helloworld.common.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.emptyPreferences
import com.example.helloworld.common.Constants.USER_PREFERENCES_FILE_NAME
import com.example.helloworld.common.datastore.UserPreferences
import com.example.helloworld.common.datastore.UserPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule
{

    @Provides
    @Singleton
    fun provideDataStorePreferences(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                    produceNewData = { emptyPreferences() }
            ) ,
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_FILE_NAME) }
    )

    @Module
    @InstallIn(SingletonComponent::class)
    interface DataModule
    {
        @Binds
        @Singleton
        fun bindUserPreferences(
            userPreferences: UserPreferencesImpl
        ): UserPreferences
    }
}