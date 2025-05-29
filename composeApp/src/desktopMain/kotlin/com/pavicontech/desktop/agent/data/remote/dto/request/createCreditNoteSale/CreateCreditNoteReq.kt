package com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale


import kotlinx.serialization.Serializable

@Serializable
data class CreateCreditNoteReq(
    val invcNo: String,
    val orgInvcNo: String,
    val salesTyCd: String,
    val rcptTyCd: String,
    val salesSttsCd: String,
    val cfmDt: String,
    val salesDt: String,
    val stockRlsDt: String,
    val cnclReqDt: String?,
    val cnclDt: String?,
    val rfdDt: String?,
    val pmtTyCd: String,
    val rfdRsnCd: String?,
    val totItemCnt: String,
    val taxRtA: String,
    val taxRtB: String,
    val taxRtC: String,
    val taxRtD: String,
    val taxRtE: String,
    val taxAmtA: String,
    val taxAmtB: String,
    val taxAmtC: String,
    val taxAmtD: String,
    val taxAmtE: String,
    val totTaxblAmt: String,
    val totTaxAmt: String,
    val totAmt: String,
    val prchrAcptcYn: String,
    val remark: String,
    val regrId: String,
    val regrNm: String,
    val modrId: String,
    val modrNm: String,
    val receipt: Receipt,
    val itemList: List<CreateCreditNoteItem>

)