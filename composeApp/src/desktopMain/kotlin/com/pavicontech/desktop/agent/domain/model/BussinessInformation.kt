package com.pavicontech.desktop.agent.domain.model

import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.compareTo

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
    val taxpayerName: String,
    val businessLogo: String? = null
){
    fun toJsonString(): String = Json.encodeToString(this)

    companion object {
        fun fromJsonString(value: String) = Json.decodeFromString<BusinessInformation>(value)
    }

}


fun String.fromBusinessJson() = Json.decodeFromString<BusinessInformation>(this)
