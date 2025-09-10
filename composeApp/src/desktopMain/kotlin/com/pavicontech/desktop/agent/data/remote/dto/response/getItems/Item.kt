package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("barcode")
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
    @SerialName("itemCategoryId")
    val itemCategoryId: Int? = null,
    @SerialName("itemClassificationCode")
    val itemClassificationCode: String,
    @SerialName("itemCode")
    val itemCode: String,
    @SerialName("itemCodeDf")
    val itemCodeDf: String,
    @SerialName("itemName")
    val itemName: String,
    @SerialName("itemType")
    val itemType: String,
    @SerialName("originCountry")
    val originCountry: String,
    @SerialName("packagingUnit")
    val packagingUnit: String,
    @SerialName("picture")
    val picture:  String? = null,
    @SerialName("price")
    val price: String,
    @SerialName("quantityUnit")
    val quantityUnit: String,
    @SerialName("status")
    val status: String,
    @SerialName("taxCode")
    val taxCode: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("userId")
    val userId: Int
){
    fun toCreateSaleItem(
        qty: Int,
        prc: Double, // this is your VAT-exclusive unit price
    ): CreateSaleItem {

        val taxRate = when (taxCode) {
            "E" -> 0.08
            "B" -> 0.16
            else -> 0.0
        }

        // Exclusive values
        val exclAmount = prc * qty
        val exclTax = exclAmount * taxRate

        // Convert to inclusive for KRA
        val inclAmount = exclAmount + exclTax
        val inclUnitPrice = inclAmount / qty

        return CreateSaleItem(
            itemSeq = 0,
            itemCd = itemCategory?.category,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = "$barcode",
            pkgUnitCd =packagingUnit ,
            qtyUnitCd = quantityUnit,
            qty = "$qty"    ,
            prc =  "$prc",
            splyAmt = "$exclAmount",
            dcRt =  "0.0",
            dcAmt = "0.0" ,
            isrcRt = null,
            isrcAmt = null,
            isrccCd = null,
            isrccNm = null,
            taxTyCd = taxCode,
            taxblAmt = "$exclAmount",
            taxAmt =  "$exclTax",
            totAmt = "$inclAmount",
            description = "null",
            id = "$id",
            itemId = id,
            itemCodeDf = itemCodeDf,

        )
    }


}
