package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import com.pavicontech.desktop.agent.domain.model.Invoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetInvoicesRes(
    @SerialName("data")
    val `data`: List<Data> = emptyList(),
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean
){
    fun toInvoices() = data.map{
        Invoice(
            id = it.id,
            branchId = it.data.branchId,
            branchName = it.data.branchName,
            itemsQty = it.data.items.sumOf { it.quantity },
            itemPrice = it.data.items.sumOf { it.amount },
            taxAmount = it.data.items.sumOf { it.taxAmount ?: 0.0 },
            totalAmount = it.data.totals.totalAmount ?: 0.0,
            subTotal = it.data.totals.subTotal,
            createdAt = it.requestTime
        )
    }
}