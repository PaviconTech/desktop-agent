package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemCategory(
    @SerialName("category")
    val category: String,
    @SerialName("description")
    val description: String,
    @SerialName("id")
    val id: Int
)