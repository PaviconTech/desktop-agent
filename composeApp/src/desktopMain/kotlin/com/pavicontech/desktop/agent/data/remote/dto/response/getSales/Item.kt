package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val bcd: String? = null,
    val createdAt: String,
    val dcAmt: String,
    val dcRt: String,
    val deletedAt: String? = null,
    val id: Int,
    val itemCd: String,
    val itemClsCd: String,
    val itemId: Int,
    val itemNm: String,
    val itemNmDef: String,
    val itemSeq: Int,
    val pkg: String,
    val pkgUnitCd: String,
    val prc: String,
    val qty: String,
    val qtyUnitCd: String,
    val salesId: Int,
    val splyAmt: String,
    val status: String,
    val taxAmt: String,
    val taxTyCd: String,
    val taxblAmt: String,
    val totAmt: String,
    val updatedAt: String
)