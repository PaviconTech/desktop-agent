package com.pavicontech.desktop.agent.data.remote.dto.request.createSale


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSaleItem(
    val itemSeq:Int,
    val itemId:Int,
    val itemCd:String? = null,
    val itemClsCd:String,
    val itemNm:String,
    val itemCodeDf:String,
    val bcd:String?,
    val pkgUnitCd:String,
    val qty:String,
    val qtyUnitCd:String,
    val prc:String,
    val splyAmt:String,
    val dcRt:String,
    val dcAmt:String,
    val isrccCd:String? = null,
    val isrccNm:String? = null,
    val isrcRt:String? = null,
    val isrcAmt:String? = null,
    val taxTyCd:String,
    val taxblAmt:String,
    val taxAmt:String,
    val totAmt:String,
    val description:String,
    val id:String,
)