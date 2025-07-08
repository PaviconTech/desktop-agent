package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes.CreditNoteRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.GetAllCreditNotesRes
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

    }

    override suspend fun createCreditNote(body: CreditNoteReq, token: String): CreditNoteRes = withContext(Dispatchers.IO) {
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
            CreditNoteRes(
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

    override suspend fun getAllCreditNotes(token: String): GetAllCreditNotesRes = withContext(Dispatchers.IO) {
        api.get(urlString = "${Constants.ETIMS_BACKEND}/credit"){
            header("Authorization", "Bearer $token")
        }.bodyAsText().let {
            it.logger(Type.DEBUG)
            Json.decodeFromString(it)
        }

    }
}