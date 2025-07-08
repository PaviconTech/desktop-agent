package com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stats(
    @SerialName("numberOfSales")
    val numberOfSales: Int,
    @SerialName("totalSales")
    val totalSales: Int,
    @SerialName("totalTax")
    val totalTax: Int
)