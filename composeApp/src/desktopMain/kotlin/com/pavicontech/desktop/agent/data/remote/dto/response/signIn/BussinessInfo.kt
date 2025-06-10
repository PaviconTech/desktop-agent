package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BussinessInfo(
    val branch: String,
    val branchId: String,
    val county: String? = null,
    val createdAt: String,
    val email: String,
    val id: Int,
    val isHeadquarter: Boolean,
    val locality: String? = null,
    val managerName: String? = "",
    val name: String,
    val phone: String,
    val pin: String,
    val status: String,
    val type: String,
    val updatedAt: String
){
    fun toBusinessInformation(): BusinessInformation = BusinessInformation(
        id = id,
        name = name,
        branchId = branchId,
        branchName = branch,
        locality = locality ?: "",
        kraPin = pin,
        county = county ?: "",
        sectorName = "",
        sdcId = "",
        taxpayerName = managerName ?: ""
    )
}