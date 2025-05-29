package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger

import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PDFExtractorRepositoryImpl(
    private val api: HttpClient
) : PDFExtractorRepository {
    override suspend fun extractInvoiceData(body: InvoiceReq): ExtractInvoiceRes {
        val result = api.post("${Constants.ETIMS_AI_BACKEND}/sales/bulk") {
            headers {
                append(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
            }
            val multiPartFormData = MultiPartFormDataContent(
                formData {
                    append("fileName", body.fileName)
                    body.file?.let {
                        append(
                            key = "file",
                            value = it, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"${body.fileName}\"")
                            }
                        )
                    }
                }
            )
            setBody(multiPartFormData)
        }.bodyAsText().let {
            it.logger(Type.INFO)
            try {
                Json.decodeFromString<ExtractInvoiceRes>(it)
            }catch (e:Exception){
                ExtractInvoiceRes(
                    status = false,
                    message = e.message ?: "An expected error occurred",
                    data = null
                )
            }
        }
        return result

    }

    override suspend fun getAllSales(): GetInvoicesRes  = withContext(Dispatchers.IO){
        api.get(urlString = "${Constants.ETIMS_AI_BACKEND}/sales/responses").body()
    }
}