package com.pavicontech.desktop.agent.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class AddItemReq(
    val itemClassificationCode: String,
    val itemName: String,
    val itemStdNm: String,
    val itemType: String,
    val originCountry: String,
    val packagingUnit: String,
    val price: String,
    val quantityUnit: String,
    val taxCode: String,
    val currentStock: Int
)