package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fee(
    @SerialName("amount")
    val amount: Double,
    @SerialName("feeDescription")
    val feeDescription: String,
    @SerialName("quantity")
    val quantity: Double
)