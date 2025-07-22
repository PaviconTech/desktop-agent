package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Item(
    @SerialName("bcd")
    val barcode: String? = null,
    @SerialName("batchNumber")
    val batchNumber: String? = null,
    @SerialName("businessId")
    val businessId: Int,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("currentStock")
    val currentStock: String,
    @SerialName("deletedAt")
    val deletedAt:  String? = null,
    @SerialName("id")
    val id: Int,
    @SerialName("ItemCategory")
    val itemCategory: ItemCategory? = null,
    @SerialName("itemCategoryId") val itemCategoryId: Int? = null,
    @SerialName("itemClsCd") val itemClassificationCode: String,
    @SerialName("itemCd") val itemCode: String,
    @SerialName("itemCdDf") val itemCodeDf: String,
    @SerialName("itemNm") val itemName: String,
    @SerialName("itemTyCd") val itemType: String,
    @SerialName("orgnNatCd") val originCountry: String? = null,
    @SerialName("pkgUnitCd") val packagingUnit: String? = null,
    @SerialName("picture") val picture:  String? = null,
    @SerialName("dftPrc") val price: String,
    @SerialName("qtyUnitCd") val quantityUnit: String? = null,
    @SerialName("status") val status: String,
    @SerialName("taxTyCd") val taxCode: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("userId") val userId: Int
){
    fun toCreateSaleItem(
        qty:Int,
        prc:Int,
        dcRt:Int,
        dcAmt:Int,
        splyAmt:Int,
        taxblAmt:Int,
        taxAmt:Int,
        totAmt:Int,
    ): CreateSaleItem {
        return CreateSaleItem(
            itemSeq = 0,
            itemCd = itemCategory?.category,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = "$barcode",
            pkgUnitCd =packagingUnit ,
            qtyUnitCd = quantityUnit ?: "",
            qty = "$qty"    ,
            prc =  "$prc",
            splyAmt = "$splyAmt",
            dcRt =  "$dcRt",
            dcAmt = "$dcAmt" ,
            isrcRt = null,
            isrcAmt = null,
            isrccCd = null,
            isrccNm = null,
            taxTyCd = taxCode,
            taxblAmt = "$taxblAmt",
            taxAmt =  "${calculateTax(taxblAmt.toDouble())}",
            totAmt = "${taxblAmt+calculateTax(taxblAmt.toDouble())}",
            description = "null",
            id = "$id",
            itemId = id,
            itemCodeDf = itemCodeDf,

        )
    }

    private fun calculateTax(amount:Double):Double{
        return when(taxCode){
            "B" -> amount * 0.16
            "E" -> amount * 0.08
            else -> 0.0
        }
    }



}
