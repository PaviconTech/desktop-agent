package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Business(
    @SerialName("bhfId")
    val branchId: String,
    @SerialName("bhfNm")
    val branchName: String,
    @SerialName("dstrtNm")
    val districtName: String,
    val id: Int,
    val name: String,
    @SerialName("pin")
    val kraPin: String,
    @SerialName("prvncNm")
    val provinceName: String,
    @SerialName("sctrNm")
    val sectorName: String,
    val sdcId: String,
    @SerialName("taxprNm")
    val taxpayerName: String
){
    fun toBusinessInformation(): BusinessInformation = BusinessInformation(
        id = id,
        name = name,
        branchId = branchId,
        branchName = branchName,
        locality = districtName,
        kraPin = kraPin,
        county = provinceName,
        sectorName = sectorName,
        sdcId = sdcId,
        taxpayerName = taxpayerName
    )
}