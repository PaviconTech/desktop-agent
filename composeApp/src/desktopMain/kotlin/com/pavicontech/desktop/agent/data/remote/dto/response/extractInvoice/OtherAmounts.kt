package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherAmounts(
    @SerialName("totalIncludingVat")
    val totalIncludingVat: Double? = null,
    @SerialName("change")
    val change: Double? = null,
    @SerialName("tendered")
    val tendered: Double?  = null
)