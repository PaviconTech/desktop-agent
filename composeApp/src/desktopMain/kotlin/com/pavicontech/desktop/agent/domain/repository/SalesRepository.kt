package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleReq
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes.CreditNoteRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.GetAllCreditNotesRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.GetSalesRes

interface SalesRepository {
    suspend fun createSale(body: CreateSaleReq, token: String): CreateSaleRes
    suspend fun createCreditNote(body: CreditNoteReq, token: String):CreditNoteRes
    suspend fun getSales(token: String): GetSalesRes
    suspend fun getAllCreditNotes(token: String): GetAllCreditNotesRes
}