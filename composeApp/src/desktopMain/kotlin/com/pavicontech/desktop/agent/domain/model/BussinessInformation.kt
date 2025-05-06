package com.pavicontech.desktop.agent.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BusinessInformation(
    val id: Int,
    val name: String,
    val branchId: String,
    val branchName: String,
    val districtName: String,
    val kraPin: String,
    val provinceName: String,
    val sectorName: String,
    val sdcId: String,
    val taxpayerName: String
){
    fun toJsonString(): String = Json.encodeToString(this)
}
