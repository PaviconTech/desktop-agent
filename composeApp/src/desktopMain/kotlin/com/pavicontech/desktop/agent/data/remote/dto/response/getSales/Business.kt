package com.pavicontech.desktop.agent.data.remote.dto.response.getSales


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Business(
    @SerialName("bhfId")
    val bhfId: String,
    @SerialName("branch")
    val branch: String,
    @SerialName("id")
    val id: Int,
    @SerialName("pin")
    val pin: String,
    @SerialName("taxprNm")
    val taxprNm: String
)