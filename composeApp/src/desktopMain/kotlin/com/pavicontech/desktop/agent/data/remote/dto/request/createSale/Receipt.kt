package com.pavicontech.desktop.agent.data.remote.dto.request.createSale


import com.pavicontech.desktop.agent.common.utils.generateTimestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    val custTin: String,
    val custMblNo: String,
    val custEmail: String ,
    val rcptPbctDt: String,
    val adrs: String,
    val rptNo: Int,
    val trdeNm: String,
    val topMsg: String,
    val btmMsg: String,
    val prchrAcptcYn: String,
)