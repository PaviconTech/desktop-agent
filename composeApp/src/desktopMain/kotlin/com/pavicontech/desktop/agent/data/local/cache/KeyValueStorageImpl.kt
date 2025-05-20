package com.pavicontech.desktop.agent.data.local.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class KeyValueStorageImpl(
    private val dataStore: DataStore<Preferences>
): KeyValueStorage {
    override suspend fun set(key: String, value: String)  = withContext(Dispatchers.IO){
        try {
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(stringPreferencesKey(key), value)
                }
            }
            println("success")
            true
        }catch (e:Exception){
            println("error $e")
            return@withContext false
        }
    }

    override suspend fun get(key: String): String? = withContext(Dispatchers.IO)  {
        val result = dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }.first()

        println("result $result")
        return@withContext result
    }

    override suspend fun clear(): Unit  = withContext(Dispatchers.IO){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override  fun observe(key: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)]
        }
    }


}