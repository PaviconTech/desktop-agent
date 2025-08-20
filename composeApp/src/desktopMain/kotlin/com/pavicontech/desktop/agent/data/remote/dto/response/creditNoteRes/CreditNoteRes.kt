package com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class CreditNoteRes(
    @SerialName("ResultMsg") val message: String,
    @SerialName("ResultCd") val status: String
)