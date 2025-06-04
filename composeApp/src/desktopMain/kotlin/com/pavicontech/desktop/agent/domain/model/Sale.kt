package com.pavicontech.desktop.agent.domain.model

import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.Receipt

data class Sale(
    val id: String,
    val customerName: String,
    val kraPin: String,
    val referenceNumber: String,
    val invoiceNumber: String,
    val receiptSign:String? = null,
    val intrlData:String? = null,
    val etimsReceiptNumber: String,
    val status: String,
    val itemsCount: Int,
    val amount: Double,
    val tax: Double,
    val createdAt: String,
)
