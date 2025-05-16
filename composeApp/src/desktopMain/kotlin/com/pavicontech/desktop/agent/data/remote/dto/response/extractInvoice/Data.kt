package com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("branchId")
    val branchId: String,
    @SerialName("branchName")
    val branchName: String,
    @SerialName("districtName")
    val districtName: String,
    @SerialName("document_id")
    val documentId: String,
    @SerialName("documentType")
    val documentType: String,
    @SerialName("fileName")
    val fileName: String,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("kraPin")
    val kraPin: String,
    @SerialName("name")
    val name: String,
    @SerialName("provinceName")
    val provinceName: String,
    @SerialName("sdcId")
    val sdcId: String,
    @SerialName("sectorName")
    val sectorName: String,
    @SerialName("taxpayerName")
    val taxpayerName: String,
    @SerialName("fees")
    val fees : List<Map<String, String>>? = emptyList(),
    @SerialName("totals")
    val totals: Totals
)