package com.pavicontech.desktop.agent.data.remote.dto.response.pullClassificationCodes

import kotlinx.serialization.Serializable

@Serializable
data class ItemCls(
    val itemClsCd: String,
    val itemClsLvl: Int,
    val itemClsNm: String,
    val mjrTgYn: String? = null,
    val taxTyCd: String? = null,
    val useYn: String
)