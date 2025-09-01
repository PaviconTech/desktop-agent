package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Totals(
    @SerialName("otherAmounts")
    val otherAmounts: Map<String, Double?>? = null,
    @SerialName("subTotal")
    val subTotal: Double?,
    @SerialName("totalAmount")
    val totalAmount: Double?,
    @SerialName("totalVat")
    val totalVat: Double? = null
){
    fun toTotals() = com.pavicontech.desktop.agent.data.local.database.entries.Totals(
        subTotal = subTotal ?: 0.0,
        totalAmount = totalAmount ?: 0.0,
        totalVat = totalVat ?: 0.0
    )

}