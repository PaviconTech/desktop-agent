package com.pavicontech.desktop.agent.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInReq(
    @SerialName("device_description")
    val deviceDescription: String,
    val password: String,
    val pin: String,
    val platform: String,
    val username: String
)