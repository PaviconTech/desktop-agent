package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.generateTimestamp
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage

import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.Receipt
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreateSaleUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend fun invoke(
        items: List<CreateSaleItem>,
        taxableAmount: Int,
        customerName:String? = null,
        customerPin:String? = null,
        branchId: String,
        businessPin:String,
        invoiceNumber: String?,
    ):CreateSaleRes {
        val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""
        val createSaleReq = CreateSaleReq(
            tin = businessPin,
            bhfId = branchId,
            custNm = customerName ?: "",
            custTin = customerPin ?: "",
            custTel = customerPin ?: "",
            custEmail = customerPin ?: "",
            checksum = invoiceNumber,
            custType = "NONE",
            custId = "",
            invcNo = "74",
            trdInvcNo = "74",
            orgInvcNo = 0,
            salesTyCd = "N",
            rcptTyCd = "S",
            pmtTyCd = "01",
            salesSttsCd = "02",
            cfmDt = generateTimestamp(),
            salesDt = getCurrentDateFormatted(),
            stockRlsDt = generateTimestamp(),
            cnclReqDt = "",
            cnclDt = "",
            rfdDt = "",
            rfdRsnCd = "",
            totItemCnt = items.size,
            taxblAmtA = 0,
            taxblAmtB = 0,
            taxblAmtC = 0,
            taxblAmtD = 0,
            taxblAmtE = 0,
            taxRtA = 0,
            taxRtB = 16,
            taxRtC = 0,
            taxRtD = 0,
            taxRtE = 0,
            taxAmtA = 0,
            taxAmtB = 0,
            taxAmtC = 0,
            taxAmtD = 0,
            taxAmtE = 0,
            totTaxblAmt = items.sumOf { it.taxblAmt},
            totTaxAmt = items.sumOf { it.taxAmt},
            totAmt = items.sumOf { it.totAmt.toDoubleOrNull() ?: 0.0 },
            prchrAcptcYn = "N",
            remark = "test2",
            regrId = "Admin",
            regrNm = "Admin",
            modrId = "Admin",
            modrNm = "Admin",
            receipt = Receipt(
                custTin = "",
                custMblNo = "",
                custEmail  = "",
                rcptPbctDt = generateTimestamp(),
                adrs = "",
                rptNo = invoiceNumber?.toIntOrNull() ?: 0 ,
                trdeNm = "",
                topMsg = "Sale of VSCU",
                btmMsg = "Bottom Sale of VSCU",
                prchrAcptcYn = "N"
            ),
            itemList = items.mapIndexed { index, item ->
                item.copy(
                    itemSeq = index + 1,
                    qty = item.qty
                )
            }
        )
        "Total Item Count: ${createSaleReq.totItemCnt}".logger(Type.INFO)
        "Total Item list size: ${createSaleReq.itemList.size}".logger(Type.INFO)
        return repository.createSale(body = createSaleReq, token)
    }



    fun getCurrentDateFormatted(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return today.format(formatter)
    }




}