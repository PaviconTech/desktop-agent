package com.pavicontech.desktop.agent.common.utils

import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue
import com.configcat.log.LogLevel
import com.pavicontech.desktop.agent.common.BuildConfig
import com.pavicontech.desktop.agent.common.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

object ConfigCatClient {

    // initialize once
    private val client = ConfigCatClient(Constants.CONFIG_CAT_KEY) {
        logLevel = LogLevel.OFF
        // Optional polling setup: auto-poll every 60s (default)
    }

    private fun user(agentId: String) = ConfigCatUser(identifier = agentId)

    suspend fun isMaintenanceMode(agentId: String): Boolean =
        client.getValue("maintenance_mode", false, user(agentId))

    fun maintenanceModeFlow(agentId: String, refreshIntervalMs: Long = 10_000): Flow<Boolean> = flow {
        while (true) {
            coroutineContext.ensureActive() // cancellation-safe checkpoint
            val isMaint = client.getValue("maintenance_mode", false, user(agentId))
            emit(isMaint)
            delay(refreshIntervalMs)
        }
    }

    suspend fun getUpdateUrl(agentId: String): String =
        client.getValue("urlUpdate", "", user(agentId))

    suspend fun getLatestVersion(agentId: String): String =
        client.getValue("latest_version", "1.0.0", user(agentId))

    suspend fun isNewVersionAvailable(agentId: String): Boolean =
        client.getValue("newVersionAvailabled", false, user(agentId))

    fun getLatestVersionFlow(agentId: String, refreshIntervalMs: Long = 10_000): Flow<String> = flow {
        while (true) {
            coroutineContext.ensureActive() // cancellation-safe checkpoint
            val isMaint =  client.getValue("latest_version", "1.0.0", user(agentId))

            emit(isMaint)
            delay(refreshIntervalMs)
        }
    }
    // Optional shutdown hook if needed
    fun close() {
        client.close()
    }
}


fun main() = runBlocking {
    val agentId = "P051234567A_nairobi-main" // test agent ID here
    val client = com.pavicontech.desktop.agent.common.utils.ConfigCatClient

    println("🔍 Testing ConfigCat flags for agentId: $agentId\n")

    val isMaint =client.isMaintenanceMode(agentId)
    println("🛠️ Maintenance Mode: $isMaint")

    val version = client.getLatestVersion(agentId)
    println("🚀 Latest Version: $version")

    val updateUrl = client.getUpdateUrl(agentId)
    println("⬇️ Update URL: $updateUrl")

    val isUpdateAvailable = client.isNewVersionAvailable(agentId)
    println("📦 New Version Available: $isUpdateAvailable")

    client.close()
    println("\n✅ Test Complete.")
}