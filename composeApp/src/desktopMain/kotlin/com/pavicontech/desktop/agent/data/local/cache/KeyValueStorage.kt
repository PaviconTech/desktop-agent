package com.pavicontech.desktop.agent.data.local.cache

import kotlinx.coroutines.flow.Flow


interface KeyValueStorage {
    suspend fun set(key: String, value: String): Boolean
    suspend fun get(key: String): String?
    suspend fun clear()
    fun observe(key: String): Flow<String?>
}