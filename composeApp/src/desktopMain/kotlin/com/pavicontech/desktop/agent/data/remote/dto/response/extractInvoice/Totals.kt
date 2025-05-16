package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Totals(
    @SerialName("otherAmounts")
    val otherAmounts: OtherAmounts,
    @SerialName("subTotal")
    val subTotal: Double,
    @SerialName("totalAmount")
    val totalAmount: Double,
    @SerialName("totalVat")
    val totalVat: Double? = null
){
    fun toTotals() = com.pavicontech.desktop.agent.data.local.database.entries.Totals(
        otherAmounts = otherAmounts,
        subTotal = subTotal,
        totalAmount = totalAmount,
        totalVat = totalVat ?: 0.0
    )

}