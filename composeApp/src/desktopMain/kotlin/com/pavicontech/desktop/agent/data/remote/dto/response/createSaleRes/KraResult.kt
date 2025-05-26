package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.Serializable

@Serializable
data class KraResult(
    val message: String,
    val result: Result,
    val status: Boolean
)