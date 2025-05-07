package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherAmounts(
    @SerialName("totalIncludingVat")
    val totalIncludingVat: Int,
    @SerialName("totalPayment")
    val totalPayment: Int
)