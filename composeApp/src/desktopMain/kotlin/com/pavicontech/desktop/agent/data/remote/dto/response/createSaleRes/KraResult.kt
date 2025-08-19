package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class KraResult(
    @SerialName("TotRcptNo") val totRcptNo: Int,
    @SerialName("RcptNo") val rcptNo: Int,
    @SerialName("IntrlData") val intrlData: String? = null,
    @SerialName("RcptSign") val rcptSign: String? = null,
    @SerialName("sdcDateTime") val sdcDateTime: String? = null,
    @SerialName("VsdcRcptPbctDate") val vsdcRcptPbctDate: String,
    @SerialName("invoiceNo") val invoiceNo: Int,
    @SerialName("SdcId") val sdcId: String,
    @SerialName("MrcNo")val mrcNo: String? = null,
    @SerialName("Qrurl")val qrUrl: String,
)