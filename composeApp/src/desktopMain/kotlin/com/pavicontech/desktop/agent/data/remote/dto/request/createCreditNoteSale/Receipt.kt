package com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    val rptNo: String,
    val trdeNm: String,
    val topMsg: String,
    val btmMsg: String,
    val prchrAcptcYn: String,
)