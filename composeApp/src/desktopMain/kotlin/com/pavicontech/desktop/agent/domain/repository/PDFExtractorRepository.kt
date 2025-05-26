package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices.GetInvoicesRes

interface PDFExtractorRepository {
    suspend fun extractInvoiceData(body: InvoiceReq): ExtractInvoiceRes
    suspend fun getAllSales(): GetInvoicesRes
}