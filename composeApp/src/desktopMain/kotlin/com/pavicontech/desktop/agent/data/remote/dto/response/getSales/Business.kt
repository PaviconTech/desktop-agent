package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import kotlinx.serialization.Serializable

@Serializable
data class Business(
    val branchId: String,
    val id: Int,
    val name: String,
    val pin: String
)