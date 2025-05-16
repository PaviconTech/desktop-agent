package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.entries.Invoice

interface InvoiceRepository {
    suspend fun insertInvoice(invoice: Invoice): Boolean
    suspend fun getInvoices(): List<Invoice>
    suspend fun getInvoiceById(id: String): Invoice?
    suspend fun updateInvoice(fileName:String, invoice: Invoice): Boolean
    suspend fun deleteInvoice(id: String): Boolean
}