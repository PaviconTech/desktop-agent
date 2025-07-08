package com.pavicontech.desktop.agent.data.remote.dto.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdjustStockReq(
    @SerialName("itemId")
    val itemId: Int,
    @SerialName("qty")
    val qty: Int,
    @SerialName("reasonId")
    val reasonId: String,
    @SerialName("remark")
    val remark: String?
)