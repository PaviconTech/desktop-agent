package com.pavicontech.desktop.agent.domain.model


data class ExtractedInvoiceData(
    val fileName: String,
    val name: String,
    val items: List<Item> = emptyList(),
    val subTotal: Double,
    val totalAmount: Double,
    val totalVat: Double? = null
    )


data class Item(
    val amount: Double,
    val itemDescription: String,
    val quantity: Double,
    val taxAmount: Double? = null,
    val taxPercentage: Double? = null,
    val taxType: String? = null
)