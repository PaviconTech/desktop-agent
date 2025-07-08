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
    @SerialName("taxAmtA") val taxAmtA: Int,
    @SerialName("taxAmtB") val taxAmtB: Int,
    @SerialName("taxAmtC") val taxAmtC: Int,
    @SerialName("taxAmtD") val taxAmtD: Int,
    @SerialName("taxAmtE") val taxAmtE: Int,
    @SerialName("taxRtA") val taxRtA: Int,
    @SerialName("taxRtB") val taxRtB: Int,
    @SerialName("taxRtC") val taxRtC: Int,
    @SerialName("taxRtD") val taxRtD: Int,
    @SerialName("taxRtE") val taxRtE: Int,
    @SerialName("taxblAmtA") val taxblAmtA: Int,
    @SerialName("taxblAmtB") val taxblAmtB: Int,
    @SerialName("taxblAmtC") val taxblAmtC: Int,
    @SerialName("taxblAmtD") val taxblAmtD: Int,
    @SerialName("taxblAmtE") val taxblAmtE: Int,
    @SerialName("totAmt") val totAmt: Int,
    @SerialName("totItemCnt") val totItemCnt: Int,
    @SerialName("totTaxAmt") val totTaxAmt: Int,
    @SerialName("totTaxblAmt") val totTaxblAmt: Int,
    @SerialName("trdInvcNo") val trdInvcNo: String
)