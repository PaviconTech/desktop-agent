package com.pavicontech.desktop.agent.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AddItemRes(
    val message: String,
    val status: Boolean
)