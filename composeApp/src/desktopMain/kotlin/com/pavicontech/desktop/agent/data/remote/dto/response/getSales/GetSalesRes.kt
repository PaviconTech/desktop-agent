package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import kotlinx.serialization.Serializable

@Serializable
data class GetSalesRes(
    val message: String,
    val sales: List<Sale> = emptyList(),
    val status: Boolean
){
    fun toSale(): List<com.pavicontech.desktop.agent.domain.model.Sale> {
        return sales.map {
            com.pavicontech.desktop.agent.domain.model.Sale(
                id = it.id.toString(),
                intrlData = it.intrlData,
                receiptSign = it.rcptSign,
                customerName = it.custNm ?: "",
                kraPin = it.custTin ?: "",
                referenceNumber = it.rcptTyCd ?: "",
                invoiceNumber = it.invcNo.toString(),
                etimsReceiptNumber = it.receipt ?: "",
                status = it.rcptTyCd ?: "",
                itemsCount = it.totItemCnt,
                amount = it.totAmt.toDoubleOrNull() ?: 0.0,
                tax = it.totTaxAmt.toDoubleOrNull() ?: 0.0,
                createdAt = it.createdAt,
            )
        }

    }
}