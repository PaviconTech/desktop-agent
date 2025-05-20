package com.pavicontech.desktop.agent.domain.model

data class Sale(
    val id: String,
    val customerName: String,
    val kraPin: String,
    val referenceNumber: String,
    val invoiceNumber: String,
    val etimsReceiptNumber: String,
    val status: String,
    val items: Int,
    val amount: Double,
    val tax: Double

)