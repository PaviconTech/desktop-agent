package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes

interface InvoiceRepository {
    suspend fun sale(body: InvoiceReq, token:String): Unit
    suspend fun getAllSales(): GetInvoicesRes
}