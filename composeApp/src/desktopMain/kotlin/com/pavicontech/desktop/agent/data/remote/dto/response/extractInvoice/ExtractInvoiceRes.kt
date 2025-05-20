package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.domain.model.ExtractedInvoiceData
import com.pavicontech.desktop.agent.domain.model.Item
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

    fun toExtractedData(): ExtractedInvoiceData{
        return ExtractedInvoiceData(
            name = data?.name ?: "",
            fileName = data?.fileName ?: "",
            items = data?.items?.map {
                Item(
                    amount = it.amount,
                    itemDescription = it.itemDescription,
                    quantity = it.quantity,
                    taxAmount = it.taxAmount,
                    taxPercentage = it.taxPercentage,
                    taxType = it.taxType
                )
            } ?: emptyList(),
            subTotal = data?.totals?.subTotal ?: 0.0,
            totalVat = data?.totals?.totalVat ?: 0.0,
            totalAmount = data?.totals?.totalAmount ?: 0.0,
        )
    }
}