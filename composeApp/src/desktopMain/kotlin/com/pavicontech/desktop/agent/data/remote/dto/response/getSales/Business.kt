package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Business(
    val branchId: String,
    val id: Int,
    val name: String,
    val pin: String
)