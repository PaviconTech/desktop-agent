package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.generateTimestamp
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteItem
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteReq

import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.Receipt
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository

class CreateCreditNoteUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend fun invoke(
        items: List<CreateCreditNoteItem>,
        taxableAmount: Int,
    ):CreateSaleRes {
        val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""
        val createSaleReq = CreateCreditNoteReq(
            invcNo = "74",
            orgInvcNo = "0",
            salesTyCd = "N",
            rcptTyCd = "R",
            pmtTyCd = "01",
            salesSttsCd = "02",
            cfmDt = generateTimestamp(),
            salesDt = "20230328",
            stockRlsDt = generateTimestamp(),
            cnclReqDt = null,
            cnclDt = null,
            rfdDt = null,
            rfdRsnCd = null,
            totItemCnt = "${items.size}",
            taxRtA = "0",
            taxRtB = "16",
            taxRtC = "0",
            taxRtD = "0",
            taxRtE = "0",
            taxAmtA = "$taxableAmount",
            taxAmtB = "0",
            taxAmtC = "0",
            taxAmtD = "0",
            taxAmtE = "0",
            totTaxblAmt = "$taxableAmount",
            totTaxAmt = "0",
            totAmt = "$taxableAmount",
            prchrAcptcYn = "N",
            remark = "",
            regrId = "11999",
            regrNm = "TEST02",
            modrId = "45678",
            modrNm = "TEST02",
            receipt = Receipt(
                rptNo = "2",
                trdeNm = "",
                topMsg = "Shopwithus",
                btmMsg = "Welcome",
                prchrAcptcYn = "N"
            ),
            itemList = items.mapIndexed { index, item ->
                item.copy(
                    itemSeq = index + 1,
                    qty = item.qty,
                    itemNmDef = item.itemNm
                )
            }
        )

        return repository.createCreditNote(body = createSaleReq, token)
    }
}