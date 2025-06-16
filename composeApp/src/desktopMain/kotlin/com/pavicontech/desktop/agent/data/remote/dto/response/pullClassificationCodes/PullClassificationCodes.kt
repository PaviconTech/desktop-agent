package com.pavicontech.desktop.agent.data.remote.dto.response.pullClassificationCodes

import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import kotlinx.serialization.Serializable

@Serializable
data class PullClassificationCodes(
    val itemClsList: List<ItemCls>
){
    fun toClassificationCodes(): List<ClassificationCode> = itemClsList.map {
        ClassificationCode(
            itemClsCd = it.itemClsCd,
            itemClsLvl = it.itemClsLvl,
            itemClsNm = it.itemClsNm,
            mjrTgYn = it.mjrTgYn,
            taxTyCd = it.taxTyCd,
            useYn = it.useYn
        )
    }
}