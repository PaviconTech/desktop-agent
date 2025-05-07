package com.pavicontech.desktop.agent.domain.model

data class Invoice(
    val id: String,
    val branchId: String,
    val branchName: String,
    val itemsQty: Double,
    val itemPrice: Double,
    val taxAmount: Double,
    val totalAmount: Double,
    val subTotal: Double?,
    val createdAt: String
)