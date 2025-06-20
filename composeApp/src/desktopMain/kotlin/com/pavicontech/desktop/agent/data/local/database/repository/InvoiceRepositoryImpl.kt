package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice



import com.pavicontech.desktop.agent.data.local.database.entries.Invoices
import com.pavicontech.desktop.agent.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class InvoiceRepositoryImpl : InvoiceRepository {

    init {
        DatabaseConfig.init()
    }

    override suspend fun getInvoicesByInvoiceNumber(invoiceNumber: String): List<Invoice> = withContext(Dispatchers.IO) {
        transaction {
            Invoices.select { Invoices.invoiceNumber eq invoiceNumber }
                .map { it.toInvoice() }
        }
    }

    override suspend fun deleteInvoiceByFileName(fileName: String) = withContext(Dispatchers.IO) {
        transaction {
            Invoices.deleteWhere { Invoices.fileName eq fileName }
        }
        return@withContext Unit
    }

    override suspend fun insertInvoice(invoice: Invoice) = withContext(Dispatchers.IO) {
        transaction {
            Invoices.insertIgnore{
                it[id] = invoice.id
                it[fileName] = invoice.fileName
                it[invoiceNumber] = invoice.invoiceNumber
                it[extractionStatus] = invoice.extractionStatus.name
                it[etimsStatus] = invoice.etimsStatus?.name
                it[items] = Json.encodeToString(invoice.items)
                it[totals] = Json.encodeToString(invoice.totals)
                it[createdAt] = invoice.createdAt
                it[updatedAt] = invoice.updatedAt ?: invoice.createdAt
            }
        }
        return@withContext Unit

    }

    override suspend fun updateInvoice(fileName: String, invoice: Invoice) = withContext(Dispatchers.IO) {
        transaction {
            Invoices.update({ Invoices.fileName eq fileName }) {
                it[id] = invoice.id
                it[invoiceNumber] = invoice.invoiceNumber
                it[extractionStatus] = invoice.extractionStatus.name
                it[etimsStatus] = invoice.etimsStatus?.name
                it[items] = Json.encodeToString(invoice.items)
                it[totals] = Json.encodeToString(invoice.totals)
                it[createdAt] = invoice.createdAt
                it[updatedAt] = invoice.updatedAt ?: invoice.createdAt
            }
        }
        return@withContext Unit
    }

    override suspend fun deleteInvoice(id: String) = withContext(Dispatchers.IO) {
        transaction {
            Invoices.deleteWhere { Invoices.id eq id }
        }
        return@withContext Unit
    }

    override suspend fun getInvoiceById(id: String): Invoice? = withContext(Dispatchers.IO) {
        transaction {
            Invoices.select { Invoices.id eq id }
                .map { it.toInvoice() }
                .singleOrNull()
        }
    }

    override suspend fun getAllInvoices(): List<Invoice> = withContext(Dispatchers.IO) {
        transaction {
            Invoices.selectAll().map { it.toInvoice() }
        }
    }

    private fun ResultRow.toInvoice(): Invoice {
        return Invoice(
            id = this[Invoices.id],
            fileName = this[Invoices.fileName],
            invoiceNumber = this[Invoices.invoiceNumber],
            extractionStatus = ExtractionStatus.valueOf(this[Invoices.extractionStatus]),
            etimsStatus = this[Invoices.etimsStatus]?.let { EtimsStatus.valueOf(it) },
            items = Json.decodeFromString(this[Invoices.items]),
            totals = Json.decodeFromString(this[Invoices.totals]),
            createdAt = this[Invoices.createdAt],
            updatedAt = this[Invoices.updatedAt]
        )
    }
}
