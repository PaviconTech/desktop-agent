package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.KraResult
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.Result
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.GetSalesRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SalesRepositoryImpl(
    private val api: HttpClient
): SalesRepository {

    override suspend fun createSale(
        body: CreateSaleReq,
        token: String
    ): CreateSaleRes = withContext(Dispatchers.IO) {
        "Creating sale".logger(Type.DEBUG)
        Json.encodeToString(body).logger(Type.INFO)

        val responseText = api.post("${Constants.ETIMS_BACKEND}/sales") {
            header("Authorization", "Bearer $token")
            setBody(body)
        }.bodyAsText()

        responseText.logger(Type.DEBUG)

        // Naively extract kraResult block as JSON string
        val kraStart = responseText.indexOf("\"kraResult\"")
        if (kraStart == -1) throw IllegalStateException("kraResult not found in response")

        val braceStart = responseText.indexOf('{', kraStart)
        var braceCount = 1
        var currentIndex = braceStart + 1

        while (braceCount > 0 && currentIndex < responseText.length) {
            when (responseText[currentIndex]) {
                '{' -> braceCount++
                '}' -> braceCount--
            }
            currentIndex++
        }

        val kraResultJson = responseText.substring(braceStart, currentIndex)
        kraResultJson.logger(Type.DEBUG)

        // Use very basic regex or substring to extract known values
        fun extractString(key: String): String {
            val pattern = """"$key"\s*:\s*"([^"]*)"""".toRegex()
            return pattern.find(kraResultJson)?.groupValues?.get(1).orEmpty()
        }

        fun extractInt(key: String): Int {
            val pattern = """"$key"\s*:\s*(\d+)""".toRegex()
            return pattern.find(kraResultJson)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        }

        val kraResult = KraResult(
            status = extractString("status").toBooleanStrictOrNull() ?: true,
            message = extractString("message"),
            result = Result(
                rcptNo = extractInt("rcptNo"),
                intrlData = extractString("intrlData"),
                rcptSign = extractString("rcptSign"),
                totRcptNo = extractInt("totRcptNo"),
                vsdcRcptPbctDate = extractString("vsdcRcptPbctDate"),
                sdcId = extractString("sdcId"),
                mrcNo = extractString("mrcNo")
            )
        )

        CreateSaleRes(
            status = kraResult.status,
            message = kraResult.message,
            kraResult = kraResult
        ).also { it.toString().logger(Type.DEBUG) }
    }



    override suspend fun getSales(token: String): GetSalesRes  = withContext(Dispatchers.Default) {
        val response = api.get(urlString = "${Constants.ETIMS_BACKEND}/sales").bodyAsText()
        response logger(Type.DEBUG)
        Json.decodeFromString(response)
    }
}