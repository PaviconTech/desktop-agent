package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteItem
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
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

class SalesRepositoryImpl(
    private val api: HttpClient
) : SalesRepository {

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
        try {
            Json.decodeFromString(responseText)
        } catch (e: Exception) {
            e.printStackTrace()
            CreateSaleRes(
                status = false,
                kraResult = null,
                message = e.message ?: "",
            )
        }
        /*
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
                    status = extractString("status").toBooleanStrictOrNull() ?: false,
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
                ).also { it.toString().logger(Type.DEBUG) }*/
    }

    override suspend fun createCreditNote(body: CreateCreditNoteReq, token: String): CreateSaleRes = withContext(Dispatchers.IO) {
        "Creating credit note".logger(Type.DEBUG)
        Json.encodeToString(body).logger(Type.INFO)

        val responseText = api.post("${Constants.ETIMS_BACKEND}/credit") {
            header("Authorization", "Bearer $token")
            setBody(body)
        }.bodyAsText()

        responseText.logger(Type.DEBUG)
        try {
            Json.decodeFromString(responseText)
        } catch (e: Exception) {
            CreateSaleRes(
                status = false,
                kraResult = null,
                message = e.message ?: "",
            )
        }

    }


    override suspend fun getSales(token: String): GetSalesRes = withContext(Dispatchers.Default) {
        api.get(urlString = "${Constants.ETIMS_BACKEND}/sales"){
            header("Authorization", "Bearer $token")
        }.bodyAsText().let {
            it.logger(Type.DEBUG)
            Json.decodeFromString(it)
        }
    }
}