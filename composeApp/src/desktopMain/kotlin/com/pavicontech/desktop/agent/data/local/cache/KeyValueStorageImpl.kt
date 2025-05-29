package com.pavicontech.desktop.agent.data.local.cache


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File


class KeyValueStorageImpl(
    private val file: File
) : KeyValueStorage {
    private val json = Json { prettyPrint = true }
    private val mutex = Mutex()

    // Cache for observe flows
    private val flowCache = mutableMapOf<String, MutableStateFlow<String?>>()

    private suspend fun loadPreferences(): MutableMap<String, String> = withContext(Dispatchers.IO) {
        if (!file.exists()) return@withContext mutableMapOf()
        try {
            val content = file.readText()
            json.decodeFromString<Map<String, String>>(content).toMutableMap()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableMapOf()
        }
    }

    private suspend fun savePreferences(preferences: Map<String, String>) = withContext(Dispatchers.IO) {
        val content = json.encodeToString(preferences)
        file.writeText(content)
    }

    override suspend fun set(key: String, value: String): Boolean = mutex.withLock {
        try {
            val prefs = loadPreferences()
            prefs[key] = value
            savePreferences(prefs)

            val flow = flowCache[key]
            if (flow != null) {
                flow.value = value
            } else {
                flowCache[key] = MutableStateFlow(value)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    override suspend fun get(key: String): String? = mutex.withLock {
        val prefs = loadPreferences()
        prefs[key]
    }

    override suspend fun clear() = mutex.withLock {
        savePreferences(emptyMap())
        flowCache.values.forEach { it.value = null }
    }

    override fun observe(key: String): Flow<String?> {
        return flowCache.getOrPut(key) {
            // Try to read from disk synchronously (risky but works if file is small)
            val initialValue = runCatching {
                if (file.exists()) {
                    val prefs = json.decodeFromString<Map<String, String>>(file.readText())
                    prefs[key]
                } else null
            }.getOrNull()
            MutableStateFlow(initialValue)
        }.asStateFlow()
    }

}