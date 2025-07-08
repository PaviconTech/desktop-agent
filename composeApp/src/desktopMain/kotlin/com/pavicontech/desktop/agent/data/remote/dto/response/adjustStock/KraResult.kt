package com.pavicontech.desktop.agent.data.remote.dto.response.adjustStock


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KraResult(
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean
)