package com.pavicontech.desktop.agent.data.remote.dto.response.adjustStock


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdjustStockRes(
    @SerialName("itemId")
    val itemId: Int,
    @SerialName("kraResult")
    val kraResult: KraResult,
    @SerialName("message")
    val message: String,
    @SerialName("newQty")
    val newQty: Int,
    @SerialName("status")
    val status: Boolean
)