package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Totals(
    @SerialName("subTotal")
    val subTotal: Double? = null,
    @SerialName("totalAmount")
    val totalAmount: Double? = null,
    @SerialName("totalVat")
    val totalVat: Double? = null
)