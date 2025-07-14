package com.pavicontech.desktop.agent.data.remote.dto.response.getSales

import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.Item
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
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
    val itemId: Int? = null,
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
){
    fun toCreditNoteItem() = com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.Item(
        bcd = bcd,
        createdAt = createdAt,
        dcAmt = dcAmt.toIntOrNull() ?: 0,
        dcRt = dcRt.toIntOrNull() ?: 0,
        deletedAt = deletedAt,
        id = id,
        itemCd = itemCd,
        itemClsCd = itemClsCd,
        itemId = itemId,
        itemNm = itemNm,
        itemNmDef = itemNmDef,
        itemSeq = itemSeq,
        pkg = pkg.toIntOrNull() ?: 0,
        pkgUnitCd = pkgUnitCd,
        prc = prc.toIntOrNull() ?: 0,
        qty = qty.toIntOrNull() ?: 0,
        qtyUnitCd = qtyUnitCd,
        salesId = salesId,
        splyAmt = splyAmt.toIntOrNull() ?: 0,
        status = status,
        taxAmt = taxAmt.toIntOrNull() ?: 0,
        taxTyCd = taxTyCd,
        taxblAmt = taxblAmt.toIntOrNull() ?: 0,
        totAmt = totAmt.toIntOrNull() ?: 0,
        updatedAt = updatedAt
    )
}