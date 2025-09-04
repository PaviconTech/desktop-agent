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
/*
    fun toCreateSaleItem(
        qty:Int,
        prc:Double,
    ): CreateSaleItem {
        fun calculateTax(amount: Double) = when (taxCode) {
            "E" -> 0.08 * amount
            "B" -> 0.16 * amount
            else -> 0.0
        }
        val taxableAmt = ,
        val taxAmount = ,
        val totalAMount =

        return CreateSaleItem(
            itemSeq = 0,
            itemCd = itemCode,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = barcode,
            pkgUnitCd =packagingUnit ,
            qtyUnitCd = quantityUnit ?: "",
            qty = qty ,
            pkg = qty,
            prc =  prc,
            splyAmt = splyAmt,
            dcRt =  dcRt.toDouble(),
            dcAmt = dcAmt.toDouble() ,
            isrcRt = null,
            isrcAmt = null,
            isrccCd = null,
            isrccNm = null,
            taxTyCd = taxCode,
            taxblAmt = taxblAmt,
            taxAmt =  calculateTax(prc),
            totAmt = "$totAmt",
            itemId = id,
            itemNmDef = itemCodeDf,

        )

    }
*/


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
            itemCd = itemCode,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = barcode,
            pkgUnitCd = packagingUnit,
            qtyUnitCd = quantityUnit ?: "",
            qty = qty,
            pkg = qty,
            // IMPORTANT: Post inclusive unit price
            prc = inclUnitPrice,
            // Supply = taxable (exclusive)
            splyAmt = exclAmount,
            dcRt = 0.0,
            dcAmt = 0.0,
            isrcRt = null,
            isrcAmt = null,
            isrccCd = null,
            isrccNm = null,
            taxTyCd = taxCode,
            taxblAmt = exclAmount,
            taxAmt = exclTax,
            // Total = inclusive (excl + VAT)
            totAmt = "$inclAmount",
            itemId = id,
            itemNmDef = itemCodeDf,
        )
    }




}
