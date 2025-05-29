package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteItem
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
    val itemCategory: ItemCategory,
    @SerialName("itemCategoryId")
    val itemCategoryId: Int,
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
            itemSeq = "0",
            itemCd = itemCategory.category,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = "$barcode",
            pkgUnitCd =packagingUnit ,
            qtyUnitCd = quantityUnit,
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
            taxAmt =  "$taxAmt",
            totAmt = "$totAmt",
            description = "null",
            id = "$id",
            itemId = id,
            itemCodeDf = itemCodeDf,

        )
    }


    fun toCreateCreditNoteItem(
        qty:Int,
        prc:Int,
        dcRt:Int,
        dcAmt:Int,
        splyAmt:Int,
        taxblAmt:Int,
        taxAmt:Int,
        totAmt:Int,
    ): CreateCreditNoteItem {
        return CreateCreditNoteItem(
            itemSeq = 0,
            itemCd = itemCategory.category,
            itemClsCd = itemClassificationCode,
            itemNm = itemName,
            bcd = "$barcode",
            pkgUnitCd =packagingUnit ,
            qtyUnitCd = quantityUnit,
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
            taxAmt =  "$taxAmt",
            totAmt = "$totAmt",
            description = "null",
            id = "$id",
            itemId = id,
            itemCodeDf = itemCodeDf,

            )
    }

}
