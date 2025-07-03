package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    companion object{
        fun toBusinessInfo(json: String): BussinessInfo {
            return try {
                if (json.isBlank()) {
                    // Return a default BussinessInfo with empty values
                    BussinessInfo(
                        branch = "",
                        branchId = "",
                        createdAt = "",
                        email = "",
                        id = 0,
                        isHeadquarter = false,
                        name = "",
                        phone = "",
                        pin = "",
                        status = "",
                        type = "",
                        updatedAt = ""
                    )
                } else {
                    Json.decodeFromString<BussinessInfo>(json)
                }
            } catch (e: Exception) {
                // Return a default BussinessInfo with empty values
                BussinessInfo(
                    branch = "",
                    branchId = "",
                    createdAt = "",
                    email = "",
                    id = 0,
                    isHeadquarter = false,
                    name = "",
                    phone = "",
                    pin = "",
                    status = "",
                    type = "",
                    updatedAt = ""
                )
            }
        }
    }
}
