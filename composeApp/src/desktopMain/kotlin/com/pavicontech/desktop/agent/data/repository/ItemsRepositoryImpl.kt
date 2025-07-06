package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.request.AddItemReq
import com.pavicontech.desktop.agent.data.remote.dto.response.AddItemRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.GetItemsRes
import com.pavicontech.desktop.agent.data.remote.dto.response.pullClassificationCodes.PullClassificationCodes
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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
            try {
                Json.decodeFromString<GetItemsRes>(it).also { itemsRes ->
                    "Items count: ${itemsRes.items.size}".logger(Type.INFO)
                    println(itemsRes)
                }
            }catch (e:Exception){
                it.logger(Type.INFO)
                throw e
            }
        }
    }

    override suspend fun pullCodes(token: String): PullCodesRes?  = withContext(Dispatchers.IO){
        return@withContext  try {
            api.get(urlString = "${Constants.ETIMS_BACKEND}/items/pullcodes"){
                headers { header("Authorization", "Bearer $token") }
            }.bodyAsText().let {
                Json.decodeFromString(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun pullClassificationCodes(token: String): PullClassificationCodes? = withContext(Dispatchers.IO){
        return@withContext  try {
            api.get(urlString = "${Constants.ETIMS_BACKEND}/items/pullclassifications"){
                headers { header("Authorization", "Bearer $token") }
            }.bodyAsText().let {
                Json.decodeFromString(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun addItem(
        token: String,
        item: AddItemReq
    ): AddItemRes = withContext(Dispatchers.IO) {
        return@withContext api.post(urlString = "${Constants.ETIMS_BACKEND}/items"){
            headers {
                header("Authorization", "Bearer $token")
            }
            setBody(item)
        }.bodyAsText().let {
            Json.decodeFromString(it)
        }
    }
}