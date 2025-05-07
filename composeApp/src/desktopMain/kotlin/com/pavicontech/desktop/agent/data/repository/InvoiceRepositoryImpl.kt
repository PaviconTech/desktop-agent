package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes
import com.pavicontech.desktop.agent.domain.repository.InvoiceRepository
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

class InvoiceRepositoryImpl(
    private val api: HttpClient
) : InvoiceRepository {
    override suspend fun sale(body: InvoiceReq, token: String) {
        val result = api.post("${Constants.ETIMS_AI_BACKEND}/sales/bulk") {
            headers {
                append(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
            }
            headers { header("token",token) }
            val multiPartFormData = MultiPartFormDataContent(
                formData {
                    append("id", body.id)
                    append("name", body.name)
                    append("branchId", body.branchId)
                    append("branchName", body.branchName)
                    append("districtName", body.districtName)
                    append("kraPin", body.kraPin)
                    append("provinceName", body.provinceName)
                    append("sectorName", body.sectorName)
                    append("sdcId", body.sdcId)
                    append("fileName", body.fileName)
                    append("taxpayerName", body.taxpayerName)
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
        }.bodyAsText()

        println(result)

    }

    override suspend fun getAllSales(): GetInvoicesRes  = withContext(Dispatchers.IO){
        api.get(urlString = "${Constants.ETIMS_AI_BACKEND}/sales/responses").body()
    }
}