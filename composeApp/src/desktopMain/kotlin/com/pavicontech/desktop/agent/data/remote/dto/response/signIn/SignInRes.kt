package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRes(
    @SerialName("access_token")
    val accessToken: String,
    val status: Boolean,
    val business: BussinessInfo
)