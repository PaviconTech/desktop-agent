package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.KtorClient
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.GetSalesRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SalesRepositoryImpl(
    private val api: HttpClient
): SalesRepository {
    override suspend fun getSales(token: String): GetSalesRes  = withContext(Dispatchers.Default) {
        val response = api.get(urlString = "${Constants.ETIMS_BACKEND}/sales").bodyAsText()
        response logger(Type.DEBUG)
        Json.decodeFromString(response)
    }
}