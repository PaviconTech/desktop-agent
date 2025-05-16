package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtractInvoiceRes(
    @SerialName("data")
    val `data`: Data? = null,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean
){
    fun toInvoice() = Invoice(
        fileName = data?.fileName ?: "",
        kraPin = data?.kraPin ?: "",
        items = data?.items?.map { it.toItem() } ?: emptyList(),
        totals = data?.totals?.toTotals()
    )
}