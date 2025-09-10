package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


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
    val taxType: String? = null,
    val discount:Double? = null,
    val discountPercentage:Double? = null
){
    fun toItem() = com.pavicontech.desktop.agent.data.local.database.entries.Item(
        amount = amount,
        itemDescription = itemDescription,
        quantity = quantity,
        taxAmount = taxAmount ?: 0.0,
        taxPercentage = taxPercentage ?: 0.0,
        taxType = taxType
    )

}