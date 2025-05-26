package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val documentType: String,
    @SerialName("fileName")
    val fileName: String,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("fees")
    val fees : List<Map<String, String>>? = emptyList(),
    @SerialName("totals")
    val totals: Totals
)