package com.pavicontech.desktop.agent.data.local.database

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import kotlinx.coroutines.runBlocking

class MongoDBConfig(private val keyValueStorage: KeyValueStorage) {

    val mongoClient: MongoClient
    private var databaseName: String = ""
    var pin: String = ""

    init {
        val url = runBlocking { buildConnectionUrl() }
        mongoClient = MongoClient.create(url)
    }

    private suspend fun buildConnectionUrl(): String {
        val businessInfo = keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            BusinessInformation.fromJsonString(it)
        }

        val username = businessInfo?.kraPin ?: ""
        val password = businessInfo?.kraPin ?: ""
        pin = businessInfo?.kraPin ?: ""
        databaseName = "${password.uppercase()}_${businessInfo?.branchId ?: "00"}"
        databaseName.logger(Type.INFO)



        val result =  keyValueStorage.get(Constants.LOCAL_MONGODB_URL)
            ?: if (username.isNotBlank() && password.isNotBlank())
                "mongodb+srv://$username:$password@etimsdesktopagent.gy6zpwc.mongodb.net/"
            else
                "mongodb://localhost:27017"

        result.logger(Type.INFO)

        return result
    }

    fun getDatabaseName(): String = databaseName
}
