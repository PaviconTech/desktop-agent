package com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class GetAllCreditNotesRes(
    @SerialName("credit") val credit: List<Credit>,
    @SerialName("message") val message: String,
    @SerialName("status") val status: Boolean
)