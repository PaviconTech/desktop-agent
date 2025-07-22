package com.pavicontech.desktop.agent.data.remote.dto.response.signIn

import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class BussinessInfo(
    val id: Int,
    @SerialName("bhfNm") val branch: String,
    @SerialName("bhfid") val branchId: String? = null,
    @SerialName("prvncNm")  val county: String? = null,
    val name: String,
    val pin: String,
){
    fun toBusinessInformation(): BusinessInformation = BusinessInformation(
        id = id,
        name = name,
        branchId = branchId ?: "",
        branchName = branch,
        locality =  "",
        kraPin = pin,
        county = county ?: "",
        sectorName = "",
        sdcId = "",
        taxpayerName = ""
    )
    companion object{
        fun toBusinessInfo(json: String): BussinessInfo {
            return try {
                if (json.isBlank()) {
                    // Return a default BussinessInfo with empty values
                    BussinessInfo(
                        branch = "",
                        branchId = "",
                        id = 0,
                        name = "",
                        pin = "",
                    )
                } else {
                    Json.decodeFromString<BussinessInfo>(json)
                }
            } catch (e: Exception) {
                // Return a default BussinessInfo with empty values
                BussinessInfo(
                    branch = "",
                    branchId = "",
                    id = 0,
                    name = "",
                    pin = "",
                )
            }
        }
    }
}
