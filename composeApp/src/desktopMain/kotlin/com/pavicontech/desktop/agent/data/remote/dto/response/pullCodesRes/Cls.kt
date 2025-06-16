package com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes

import kotlinx.serialization.Serializable

@Serializable
data class Cls(
    val cdCls: String,
    val cdClsDesc: String? = null,
    val cdClsNm: String,
    val dtlList: List<Dtl>,
    val useYn: String,
    val userDfnNm1: String? = null,
    val userDfnNm2: String? = null,
    val userDfnNm3: String? = null
)