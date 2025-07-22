package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Business(
    @SerialName("bhfId") val branchId: String,
    @SerialName("id") val id: Int,
    @SerialName("branch") val name: String,
    @SerialName("pin") val pin: String
)