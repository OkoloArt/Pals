package com.example.helloworld.common.datastore

import android.app.Notification
import kotlinx.coroutines.flow.Flow

interface UserPreferences {

    val uid: Flow<String?>
    suspend fun saveUid(uid: String)

    val token: Flow<String?>
    suspend fun saveToken(token: String)

    val dayNightTheme: Flow<Boolean?>
    suspend fun saveDayNightTheme(dayNightTheme: Boolean)

    val allowNotifications: Flow<Boolean?>
    suspend fun saveAllowNotifications(notification: Boolean)
}