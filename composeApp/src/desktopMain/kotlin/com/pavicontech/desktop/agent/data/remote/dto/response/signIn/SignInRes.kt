package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class SignInRes(
    @SerialName("access_token")
    val accessToken: String,
    val status: Boolean,
    val business: BussinessInfo
)