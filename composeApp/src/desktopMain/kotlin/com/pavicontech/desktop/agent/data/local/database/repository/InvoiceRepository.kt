package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.entries.Item
import com.pavicontech.desktop.agent.data.local.database.entries.Totals


    interface InvoiceRepository {
        suspend fun getInvoicesByInvoiceNumber(invoiceNumber: String): List<Invoice>
        suspend fun deleteInvoiceByFileName(fileName: String)
        suspend fun insertInvoice(invoice: Invoice)
        suspend fun updateInvoice(fileName: String, invoice: Invoice)
        suspend fun deleteInvoice(id: String)
        suspend fun getInvoiceById(id: String): Invoice?
        suspend fun getAllInvoices(): List<Invoice>
    }

