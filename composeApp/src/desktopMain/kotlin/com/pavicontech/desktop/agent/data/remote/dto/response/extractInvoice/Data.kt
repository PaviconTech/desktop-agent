package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Data(
    val documentType: String,
    @SerialName("fileName") val fileName: String,
    @SerialName("items") val items: List<Item>,
    /*  @SerialName("fees")
      val fees : List<Map<String, String>>? = emptyList(),*/
    @SerialName("customerName") val customerName:String?,
    @SerialName("customerPin") val customerPin:String?,
    @SerialName("totals") val totals: Totals
)