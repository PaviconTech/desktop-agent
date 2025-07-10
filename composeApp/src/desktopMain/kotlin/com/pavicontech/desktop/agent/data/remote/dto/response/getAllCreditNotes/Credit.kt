package com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class Credit(
    @SerialName("businessId") val businessId: Int,
    @SerialName("cfmDt") val cfmDt: String,
    @SerialName("cnclDt") val cnclDt: String? = null,
    @SerialName("cnclReqDt") val cnclReqDt: String? = null,
    @SerialName("createdAt") val createdAt: String,
   // @SerialName("CreditNoteReceipt") val creditNoteReceipt: String? = null,
    @SerialName("creditNoteReceiptId") val creditNoteReceiptId: Int? = null,
    @SerialName("curRcptNo") val curRcptNo: String? = null,
    @SerialName("custNm") val custNm: String? = null,
    @SerialName("custTin") val custTin: String? = null,
    @SerialName("deletedAt") val deletedAt: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("intrlData") val intrlData: String? = null,
    @SerialName("invcNo") val invcNo: Int,
    @SerialName("items") val items: List<Item>,
    @SerialName("long_url") val longUrl: String? = null,
    @SerialName("orgInvcNo") val orgInvcNo: Int,
    @SerialName("PRINT") val pRINT: String,
    @SerialName("pmtTyCd") val pmtTyCd: String,
    @SerialName("prchrAcptcYn") val prchrAcptcYn: String,
    @SerialName("rcptSign") val rcptSign: String? = null,
    @SerialName("rcptTyCd") val rcptTyCd: String,
    @SerialName("remark") val remark: String,
    @SerialName("resultDt") val resultDt: String? = null,
    @SerialName("rfdDt") val rfdDt: String? = null,
    @SerialName("rfdRsnCd") val rfdRsnCd: String?,
    @SerialName("salesDt") val salesDt: String,
    @SerialName("salesSttsCd") val salesSttsCd: String,
    @SerialName("salesTyCd") val salesTyCd: String,
    @SerialName("sdcDateTime") val sdcDateTime: String? = null,
    @SerialName("short_id") val shortId: String? = null,
    @SerialName("short_url") val shortUrl: String? = null,
    @SerialName("status") val status: String,
    @SerialName("stockRlsDt") val stockRlsDt: String,
    @SerialName("taxAmtA") val taxAmtA: String,
    @SerialName("taxAmtB") val taxAmtB: String,
    @SerialName("taxAmtC") val taxAmtC: String,
    @SerialName("taxAmtD") val taxAmtD: String,
    @SerialName("taxAmtE") val taxAmtE: String,
    @SerialName("taxRtA") val taxRtA: String,
    @SerialName("taxRtB") val taxRtB: String,
    @SerialName("taxRtC") val taxRtC: String,
    @SerialName("taxRtD") val taxRtD: String,
    @SerialName("taxRtE") val taxRtE: String,
    @SerialName("taxblAmtA") val taxblAmtA: String,
    @SerialName("taxblAmtB") val taxblAmtB: String,
    @SerialName("taxblAmtC") val taxblAmtC: String,
    @SerialName("taxblAmtD") val taxblAmtD: String,
    @SerialName("taxblAmtE") val taxblAmtE: String,
    @SerialName("totAmt") val totAmt: String,
    @SerialName("totItemCnt") val totItemCnt: Int,
    @SerialName("totRcptNo") val totRcptNo: String? = null,
    @SerialName("totTaxAmt") val totTaxAmt: String,
    @SerialName("totTaxblAmt") val totTaxblAmt: String,
    @SerialName("trdInvcNo") val trdInvcNo: Int,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("userId") val userId: Int
)