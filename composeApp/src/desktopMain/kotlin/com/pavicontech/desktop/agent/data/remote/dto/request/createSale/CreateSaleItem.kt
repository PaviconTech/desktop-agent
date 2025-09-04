package com.pavicontech.desktop.agent.data.remote.dto.request.createSale


import kotlinx.serialization.Serializable

@Serializable
data class CreateSaleItem(
    val itemSeq:Int,
    val itemId:Int,
    val itemCd:String? = null,
    val itemClsCd:String,
    val itemNm:String,
    val itemNmDef:String,
    val bcd:String?,
    val pkgUnitCd:String? = null,
    val qty:Int,
    val pkg:Int,
    val qtyUnitCd:String,
    val prc: Double,
    val splyAmt:Double,
    val dcRt:Double,
    val dcAmt:Double,
    val isrccCd:String? = null,
    val isrccNm:String? = null,
    val isrcRt:String? = null,
    val isrcAmt:Double? = null,
    val taxTyCd:String,
    val taxblAmt:Double,
    val taxAmt:Double,
    val totAmt:String,
)