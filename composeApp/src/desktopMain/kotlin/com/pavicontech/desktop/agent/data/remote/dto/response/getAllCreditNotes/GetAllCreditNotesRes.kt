package com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetAllCreditNotesRes(
    @SerialName("credit") val credit: List<Credit>,
    @SerialName("message") val message: String,
    @SerialName("status") val status: Boolean
)