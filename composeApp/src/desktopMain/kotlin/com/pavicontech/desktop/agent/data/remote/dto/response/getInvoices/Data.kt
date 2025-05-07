package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("data")
    @Serializable(with = DataXSerializer::class)
    val `data`: DataX,
    @SerialName("document_id")
    val documentId: String,
    @SerialName("id")
    val id: String,
    @SerialName("message")
    val message: String,
    @SerialName("request_time")
    val requestTime: String,
    @SerialName("status")
    val status: Boolean
)