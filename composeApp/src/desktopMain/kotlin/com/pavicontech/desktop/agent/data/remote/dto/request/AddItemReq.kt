package com.pavicontech.desktop.agent.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddItemReq(
    @SerialName("itemClsCd") val itemClassificationCode: String,
    @SerialName("itemCdDf")  val itemClassDefinition: String,
    @SerialName ("itemTyCd") val itemType: String,
    @SerialName ("itemNm")  val itemName: String,
    @SerialName ("itemCd")  val itemCode: String,
    val itemStdNm: String,
    @SerialName("orgnNatCd") val originCountry: String,
    @SerialName("pkgUnitCd") val packagingUnit: String,
    @SerialName("dftPrc") val price: String,
    @SerialName("qtyUnitCd") val quantityUnit: String,
    @SerialName ("taxTyCd")  val taxCode: String,
    @SerialName("currentStock") val currentStock: Int
)