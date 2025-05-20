package com.pavicontech.desktop.agent.data.remote.dto.response.getSales


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    @SerialName("adrs")
    val adrs: String,
    @SerialName("btmMsg")
    val btmMsg: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("custEmail")
    val custEmail: String,
    @SerialName("custMblNo")
    val custMblNo: String,
    @SerialName("custTin")
    val custTin: String,
    @SerialName("customerId")
    val customerId: Int,
    @SerialName("deletedAt")
    val deletedAt: String? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("prchrAcptcYn")
    val prchrAcptcYn: String,
    @SerialName("rcptPbctDt")
    val rcptPbctDt: String,
    @SerialName("rptNo")
    val rptNo: Int,
    @SerialName("status")
    val status: String,
    @SerialName("topMsg")
    val topMsg: String,
    @SerialName("trdeNm")
    val trdeNm: String,
    @SerialName("updatedAt")
    val updatedAt: String
)