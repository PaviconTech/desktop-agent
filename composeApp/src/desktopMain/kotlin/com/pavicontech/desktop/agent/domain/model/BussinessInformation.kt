package com.pavicontech.desktop.agent.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BusinessInformation(
    val id: Int,
    val name: String,
    val branchId: String,
    val branchName: String,
    val locality: String,
    val kraPin: String,
    val county: String,
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
