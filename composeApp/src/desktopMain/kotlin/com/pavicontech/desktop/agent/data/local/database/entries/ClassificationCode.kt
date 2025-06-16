package com.pavicontech.desktop.agent.data.local.database.entries




data class ClassificationCode(
    val itemClsCd: String,
    val itemClsNm: String,
    val itemClsLvl: Int,
    val taxTyCd: String? = null,
    val mjrTgYn: String? = null,
    val useYn: String
)