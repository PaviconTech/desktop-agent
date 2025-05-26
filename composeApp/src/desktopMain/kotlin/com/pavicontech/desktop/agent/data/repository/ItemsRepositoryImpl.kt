package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.GetItemsRes
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ItemsRepositoryImpl(private val api: HttpClient) : ItemsRepository {
    override suspend fun getItems(token: String): GetItemsRes = withContext(Dispatchers.IO) {
        return@withContext  api.get(urlString = "${Constants.ETIMS_BACKEND}/items") {
            headers {
                header("Authorization", "Bearer $token")
            }
        }.bodyAsText().let {
            it.logger(Type.DEBUG)
            Json.decodeFromString<GetItemsRes>(it)
        }
    }
}