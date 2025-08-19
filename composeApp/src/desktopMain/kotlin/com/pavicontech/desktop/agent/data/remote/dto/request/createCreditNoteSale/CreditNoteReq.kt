package com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditNoteReq(
    @SerialName("cfmDt") val cfmDt: String,
    @SerialName("custEmail") val custEmail: String,
    @SerialName("custNm") val custNm: String,
    @SerialName("custTel") val custTel: String,
    @SerialName("custTin") val custTin: String,
    @SerialName("custType") val custType: String,
    @SerialName("invcNo") val invcNo: String,
    @SerialName("itemList") val itemList: List<Item>,
    @SerialName("modrId") val modrId: String,
    @SerialName("modrNm") val modrNm: String,
    @SerialName("orgInvcNo") val orgInvcNo: Int,
    @SerialName("pmtTyCd") val pmtTyCd: String,
    @SerialName("prchrAcptcYn") val prchrAcptcYn: String,
    @SerialName("rcptTyCd") val rcptTyCd: String,
    @SerialName("regrId") val regrId: String,
    @SerialName("regrNm") val regrNm: String,
    @SerialName("remark") val remark: String,
    @SerialName("rfdRsnCd") val rfdRsnCd: String,
    @SerialName("salesDt") val salesDt: String,
    @SerialName("salesSttsCd") val salesSttsCd: String,
    @SerialName("salesTyCd") val salesTyCd: String,
    @SerialName("stockRlsDt") val stockRlsDt: String,
    @SerialName("taxAmtA") val taxAmtA: Double,
    @SerialName("taxAmtB") val taxAmtB: Double,
    @SerialName("taxAmtC") val taxAmtC: Double,
    @SerialName("taxAmtD") val taxAmtD: Double,
    @SerialName("taxAmtE") val taxAmtE: Double,
    @SerialName("taxRtA") val taxRtA: Double,
    @SerialName("taxRtB") val taxRtB: Double,
    @SerialName("taxRtC") val taxRtC: Double,
    @SerialName("taxRtD") val taxRtD: Double,
    @SerialName("taxRtE") val taxRtE: Double,
    @SerialName("taxblAmtA") val taxblAmtA: Double,
    @SerialName("taxblAmtB") val taxblAmtB: Double,
    @SerialName("taxblAmtC") val taxblAmtC: Double,
    @SerialName("taxblAmtD") val taxblAmtD: Double,
    @SerialName("taxblAmtE") val taxblAmtE: Double,
    @SerialName("totAmt") val totAmt: Double,
    @SerialName("totItemCnt") val totItemCnt: Double,
    @SerialName("totTaxAmt") val totTaxAmt: Double,
    @SerialName("trdInvcNo") val trdInvcNo: String
)