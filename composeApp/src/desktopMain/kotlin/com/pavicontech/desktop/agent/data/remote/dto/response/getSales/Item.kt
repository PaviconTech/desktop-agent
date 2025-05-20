package com.pavicontech.desktop.agent.data.remote.dto.response.getSales


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("bcd")
    val bcd: String,
    @SerialName("charges")
    val charges: String? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("dcAmt")
    val dcAmt: String,
    @SerialName("dcRt")
    val dcRt: String,
    @SerialName("deletedAt")
    val deletedAt: String? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("itemCd")
    val itemCd: String,
    @SerialName("itemClsCd")
    val itemClsCd: String,
    @SerialName("itemId")
    val itemId: Int,
    @SerialName("itemNm")
    val itemNm: String,
    @SerialName("itemNmDef")
    val itemNmDef: String,
    @SerialName("itemSeq")
    val itemSeq: Int,
    @SerialName("pkg")
    val pkg: String,
    @SerialName("pkgUnitCd")
    val pkgUnitCd: String,
    @SerialName("prc")
    val prc: String,
    @SerialName("qty")
    val qty: String,
    @SerialName("qtyUnitCd")
    val qtyUnitCd: String,
    @SerialName("salesId")
    val salesId: Int,
    @SerialName("splyAmt")
    val splyAmt: String,
    @SerialName("status")
    val status: String,
    @SerialName("taxAmt")
    val taxAmt: String,
    @SerialName("taxTyCd")
    val taxTyCd: String,
    @SerialName("taxblAmt")
    val taxblAmt: String,
    @SerialName("totAmt")
    val totAmt: String,
    @SerialName("updatedAt")
    val updatedAt: String
)