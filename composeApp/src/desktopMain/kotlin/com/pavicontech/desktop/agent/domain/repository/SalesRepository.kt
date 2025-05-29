package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteItem
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreateCreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.GetSalesRes

interface SalesRepository {
    suspend fun createSale(body: CreateSaleReq, token: String): CreateSaleRes
    suspend fun createCreditNote(body: CreateCreditNoteReq, token: String):CreateSaleRes
    suspend fun getSales(token: String): GetSalesRes
}