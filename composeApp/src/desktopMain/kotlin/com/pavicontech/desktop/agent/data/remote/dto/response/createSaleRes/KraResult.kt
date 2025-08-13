package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KraResult(
    @SerialName("rcptNo") val rcptNo: Int,
    @SerialName("intrlData") val intrlData: String? = null,
    @SerialName("rcptSign") val rcptSign: String? = null,
    @SerialName("totRcptNo") val totRcptNo: Int,
    @SerialName("vsdcRcptPbctDate") val vsdcRcptPbctDate: String,
    @SerialName("sdcId") val sdcId: String,
    @SerialName("mrcNo")val mrcNo: String,
)