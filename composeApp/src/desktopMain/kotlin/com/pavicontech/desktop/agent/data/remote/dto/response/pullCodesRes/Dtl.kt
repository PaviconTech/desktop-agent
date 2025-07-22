package com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Dtl(
    val cd: String,
    val cdNm: String,
    val cdDesc: String? = null,
    val srtOrd: Int,
    val useYn: String,
    val userDfnCd1: String? =null,
    val userDfnCd2: String? = null,
    val userDfnCd3: String? = null
)