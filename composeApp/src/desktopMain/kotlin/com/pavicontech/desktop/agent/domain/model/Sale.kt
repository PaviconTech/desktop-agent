package com.pavicontech.desktop.agent.domain.model

import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.Receipt
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Item

data class Sale(
    val id: String,
    val customerName: String,
    val kraPin: String,
    val referenceNumber: String,
    val invoiceNumber: String,
    val receiptSign:String? = null,
    val intrlData:String? = null,
    val qrUrl:String,
    val etimsReceiptNumber: String,
    val status: String,
    val itemsCount: Int,
    val amount: Double,
    val tax: Double,
    val createdAt: String,
    val items:List<Item>
)

