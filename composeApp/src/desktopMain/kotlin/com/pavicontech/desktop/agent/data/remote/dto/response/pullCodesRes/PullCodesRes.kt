package com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PullCodesRes(
    val clsList: List<Cls>
){
    fun toJson() = Json.encodeToString(this)
    companion object{
        fun fromJson(json: String) = Json.decodeFromString<PullCodesRes>(json)
    }
}