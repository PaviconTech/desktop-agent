package com.pavicontech.desktop.agent.data.remote.dto.response.pullClassificationCodes

import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class PullClassificationCodes(
    val kraResult: KraResult
){
    fun toClassificationCodes(): List<ClassificationCode> = kraResult.itemClsList.map {
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


@Serializable
data class KraResult(
    val itemClsList:List<ItemCls>
)