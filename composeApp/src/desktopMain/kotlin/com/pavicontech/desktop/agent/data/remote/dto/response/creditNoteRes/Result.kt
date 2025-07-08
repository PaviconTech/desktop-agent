package com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerialName("intrlData")
    val intrlData: String,
    @SerialName("mrcNo")
    val mrcNo: String,
    @SerialName("rcptNo")
    val rcptNo: Int,
    @SerialName("rcptSign")
    val rcptSign: String,
    @SerialName("sdcId")
    val sdcId: String,
    @SerialName("totRcptNo")
    val totRcptNo: Int,
    @SerialName("vsdcRcptPbctDate")
    val vsdcRcptPbctDate: String
)