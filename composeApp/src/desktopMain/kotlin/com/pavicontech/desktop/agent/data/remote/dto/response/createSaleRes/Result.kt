package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val intrlData: String,
    val mrcNo: String,
    val rcptNo: Int,
    val rcptSign: String,
    val sdcId: String,
    val totRcptNo: Int,
    val vsdcRcptPbctDate: String
)