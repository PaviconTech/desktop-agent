package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KraResult(
    @SerialName("IntrlData") val intrlData: String? = null,
    @SerialName("MrcNo")val mrcNo: String,
    //@SerialName("RcptNo") val rcptNo: Int,
    @SerialName("RcptSign") val rcptSign: String? = null,
    @SerialName("SdcId") val sdcId: String,
    @SerialName("TotRcptNo") val totRcptNo: Int,
    @SerialName("VsdcRcptPbctDate") val vsdcRcptPbctDate: String
)