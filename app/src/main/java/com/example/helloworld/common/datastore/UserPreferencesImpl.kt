package com.example.helloworld.common.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val dataStorePreferences: DataStore<Preferences>) : UserPreferences
{

    private val tag = this::class.java.simpleName

    override val uid: Flow<String?> = dataStorePreferences.data
        .catch { exception ->
            exception.localizedMessage?.let { Log.e(tag , it) }
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PreferencesKeys.UID]
        }

    override suspend fun saveUid(uid: String) {
        dataStorePreferences.edit { preferences ->
            preferences[PreferencesKeys.UID] = uid
        }
    }

    override val token: Flow<String?> = dataStorePreferences.data
        .catch { exception ->
            exception.localizedMessage?.let { Log.e(tag , it) }
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PreferencesKeys.TOKEN]
        }

    override suspend fun saveToken(token: String) {
        dataStorePreferences.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    override val dayNightTheme: Flow<Boolean?> = dataStorePreferences.data
        .catch { exception ->
            exception.localizedMessage?.let { Log.e(tag , it) }
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PreferencesKeys.DAYNIGHT]
        }

    override suspend fun saveDayNightTheme(dayNightTheme: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[PreferencesKeys.DAYNIGHT] = dayNightTheme
        }
    }

    override val allowNotifications: Flow<Boolean?> = dataStorePreferences.data
        .catch { exception ->
            exception.localizedMessage?.let { Log.e(tag , it) }
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[PreferencesKeys.NOTIFICATION]
        }

    override suspend fun saveAllowNotifications(notification: Boolean) {
        dataStorePreferences.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION] = notification
        }
    }

    private object PreferencesKeys {
        val UID = stringPreferencesKey(name = "uid")
        val TOKEN = stringPreferencesKey(name = "token")
        val DAYNIGHT = booleanPreferencesKey(name = "dayNight")
        val NOTIFICATION = booleanPreferencesKey(name ="notifications")
    }
}