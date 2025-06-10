package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class CreateSaleRes(
    val kraResult: KraResult? = null,
    val message: String,
    val status: Boolean,
)