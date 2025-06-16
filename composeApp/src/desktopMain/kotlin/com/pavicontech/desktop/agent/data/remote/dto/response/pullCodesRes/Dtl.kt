package com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes

import kotlinx.serialization.Serializable

@Serializable
data class Dtl(
    val cd: String,
    val cdDesc: String? = null,
    val cdNm: String,
    val srtOrd: Int,
    val useYn: String,
    val userDfnCd1: String? =null,
    val userDfnCd2: String? = null,
    val userDfnCd3: String? = null
)