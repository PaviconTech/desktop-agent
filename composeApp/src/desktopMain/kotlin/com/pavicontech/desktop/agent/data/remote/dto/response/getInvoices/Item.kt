package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("amount")
    val amount: Double,
    @SerialName("itemDescription")
    val itemDescription: String,
    @SerialName("quantity")
    val quantity: Double,
    @SerialName("taxAmount")
    val taxAmount: Double? = null,
    @SerialName("taxPercentage")
    val taxPercentage: Double? = null,
    @SerialName("taxType")
    val taxType: String? = null
)