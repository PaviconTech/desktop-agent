package com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class PullCodesRes(
    val kraResult: KraResult
) {
    fun toJson() = Json.encodeToString(this)

    companion object {
        fun fromJson(json: String) = Json.decodeFromString<PullCodesRes>(json)
    }
}

@Serializable
data class KraResult(
    val clsList: List<Cls>
)