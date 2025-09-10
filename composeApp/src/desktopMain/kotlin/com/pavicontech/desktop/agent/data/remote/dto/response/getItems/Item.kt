package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.math.BigDecimal
import java.math.RoundingMode

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
    val deletedAt: String? = null,
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
    @SerialName("picture") val picture: String? = null,
    @SerialName("dftPrc") val price: String,
    @SerialName("qtyUnitCd") val quantityUnit: String? = null,
    @SerialName("status") val status: String,
    @SerialName("taxTyCd") val taxCode: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("userId") val userId: Int
) {


    fun toCreateSaleItem(
        qty: Int,
        prc: Double,                // VAT-exclusive unit price
        dcRt: Double = 0.0,
        lineAMount: Double,
    ): CreateSaleItem {
        val taxRate = when (taxCode) {
            "E" -> 0.08
            "B" -> 0.16
            else -> 0.0
        }

        // --- Customer discount calculation ---
        val dcAmtPerUnit = lineAMount / qty
        val totalDiscount = dcAmtPerUnit * qty
        val netUnitPrice = prc - dcAmtPerUnit
        val taxblAmtCustomer = netUnitPrice * qty
        val taxAmtCustomer = taxblAmtCustomer * taxRate
        val splyAmtCustomer = taxblAmtCustomer + taxAmtCustomer
        val totAmtCustomer = splyAmtCustomer

        // --- KRA calculation (ignores discounts) ---
        val taxblAmtKra = prc * qty
        val taxAmtKra = taxblAmtKra * taxRate
        val totAmtKra = taxblAmtKra + taxAmtKra
        //val price = (prc * 100) / 84
        //val splyAmt = (price - dcAmtPerUnit) * qty
        //val splyAmt = (((prc - dcAmtPerUnit) * 100 ) / 84) * qty
        //val taxableAmt = splyAmt / 1.16
        //val taxAmt = splyAmt - taxableAmt

        //New code from line 75 - 79
        val price = ((prc - dcAmtPerUnit) * 1.16) //Price of the item with VAT and already discounted
        val splyAmt = price * qty
        val taxableAmt = splyAmt / 1.16
        val taxAmt = splyAmt - taxableAmt

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
            prc = price,                         // unit price excl. VAT
            splyAmt = splyAmt.to2dp(),        // what customer sees
            dcRt = dcRt,                      // discount %
            dcAmt = totalDiscount.to2dp(),            // total discount
            taxTyCd = taxCode,
            // 👉 Decide which values to pass depending on target (Invoice vs KRA)
            taxblAmt = taxableAmt.to2dp(),           // send KRA value (no discount)
            taxAmt = taxAmt.to2dp(),               // send KRA value (no discount)
            totAmt = "${splyAmt.to2dp()}",            // send KRA value (no discount)

            isrcRt = null,
            isrcAmt = null,
            isrccCd = null,
            isrccNm = null,
            itemId = id,
            itemNmDef = itemCodeDf,
        )
    }


}


fun Double.to2dp(): Double =
    BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
