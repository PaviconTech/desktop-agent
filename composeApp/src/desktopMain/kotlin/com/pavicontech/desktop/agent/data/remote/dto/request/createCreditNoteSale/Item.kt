package com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("bcd") val bcd: String?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("dcAmt") val dcAmt: Int?,
    @SerialName("dcRt") val dcRt: Int,
    @SerialName("deletedAt") val deletedAt: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("itemCd") val itemCd: String,
    @SerialName("itemClsCd") val itemClsCd: String,
    @SerialName("itemId") val itemId: Int?,
    @SerialName("itemNm") val itemNm: String,
    @SerialName("itemNmDef") val itemNmDef: String,
    @SerialName("itemSeq") val itemSeq: Int,
    @SerialName("pkg") val pkg: Int,
    @SerialName("pkgUnitCd") val pkgUnitCd: String,
    @SerialName("prc") val prc: Int,
    @SerialName("qty") val qty: Int,
    @SerialName("qtyUnitCd") val qtyUnitCd: String,
    @SerialName("salesId") val salesId: Int,
    @SerialName("splyAmt") val splyAmt: Int,
    @SerialName("status") val status: String,
    @SerialName("taxAmt") val taxAmt: Int,
    @SerialName("taxTyCd") val taxTyCd: String,
    @SerialName("taxblAmt") val taxblAmt: Int,
    @SerialName("totAmt") val totAmt: Int,
    @SerialName("updatedAt") val updatedAt: String
)