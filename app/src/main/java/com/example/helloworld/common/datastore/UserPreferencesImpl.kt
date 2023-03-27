package com.example.helloworld.common.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
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
        }
        .map { preferences ->
            preferences[PreferencesKeys.UID]
        }

    override suspend fun saveUid(uid: String)
    {
        dataStorePreferences.edit { preferences ->
            preferences[PreferencesKeys.UID] = uid
        }
    }

    private object PreferencesKeys {
        val UID = stringPreferencesKey(name = "uid")
    }
}