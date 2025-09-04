package com.pavicontech.desktop.agent.data.remote.dto.request.createSale


import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.LongAsStringSerializer
import kotlin.text.toInt

@Serializable
data class CreateSaleReq(
    val tin: String,
    val bhfId: String,
    val custTin: String,
    val custNm: String,
    val custEmail: String,
    val custTel: String,
    val checksum: String? = null,
    val custId: String,
    val custType: String,
    val invcNo: String,
    val trdInvcNo: String,
    val orgInvcNo: Int,
    val salesTyCd: String,
    val rcptTyCd: String,
    val salesSttsCd: String,
    val cfmDt: String,
    val salesDt: String,
    val stockRlsDt: String,
    val cnclReqDt: String,
    val cnclDt: String,
    val rfdDt: String,
    val pmtTyCd: String,
    val rfdRsnCd: String,
    val taxblAmtA: Int,
    val taxblAmtB: Double,
    val taxblAmtC: Int,
    val taxblAmtD: Int,
    val taxblAmtE: Int,
    val totItemCnt: Int,
    val taxRtA: Int,
    val taxRtB: Int,
    val taxRtC: Int,
    val taxRtD: Int,
    val taxRtE: Int,
    val taxAmtA: Int? = null,
    val taxAmtB: Int,
    val taxAmtC: Int,
    val taxAmtD: Int,
    val taxAmtE: Int,
    val totTaxblAmt: Double,
    val totTaxAmt: Double,
    val totAmt: Double,
    val prchrAcptcYn: String,
    val remark: String,
    val regrId: String,
    val regrNm: String,
    val modrId: String,
    val modrNm: String,
    val inclusiveYn: String,
    val receipt: Receipt,
    val itemList: List<CreateSaleItem>

)