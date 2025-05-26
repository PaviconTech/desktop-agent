package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.Serializable

@Serializable
data class Stats(
    val numberOfSales: Int,
    val totalSales: Int,
    val totalTax: Int
)