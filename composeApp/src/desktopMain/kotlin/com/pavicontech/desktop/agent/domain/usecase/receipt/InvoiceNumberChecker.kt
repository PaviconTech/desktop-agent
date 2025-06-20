package com.pavicontech.desktop.agent.domain.usecase.receipt

import androidx.compose.runtime.mutableStateListOf
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.local.database.entries.InvoiceEntries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


class InvoiceNumberChecker {

    init {
        DatabaseConfig.init()
    }

    suspend fun doesInvoiceExist(invoiceNumber: String?): Boolean = withContext(Dispatchers.IO) {
        if (invoiceNumber.isNullOrBlank()) return@withContext false

        val exists = transaction {
            InvoiceEntries.selectAll().where { InvoiceEntries.invoiceNumber eq invoiceNumber }.count() > 0
        }

        "Invoice Number: $invoiceNumber, Exists: $exists".logger(Type.INFO)
        return@withContext exists
    }

    suspend fun addInvoice(invoiceNumber: String?, fileName: String) = withContext(Dispatchers.IO) {
        if (invoiceNumber.isNullOrBlank()) return@withContext

        transaction {
            // Skip if already exists
            val exists = InvoiceEntries.select {
                InvoiceEntries.invoiceNumber eq invoiceNumber
            }.count() > 0

            if (!exists) {
                InvoiceEntries.insert {
                    it[InvoiceEntries.invoiceNumber] = invoiceNumber
                    it[InvoiceEntries.fileName] = fileName
                }
            }
        }
    }

    suspend fun getFileName(invoiceNumber: String?): String? = withContext(Dispatchers.IO) {
        if (invoiceNumber.isNullOrBlank()) return@withContext null

        return@withContext transaction {
            InvoiceEntries
                .selectAll().where { InvoiceEntries.invoiceNumber eq invoiceNumber }
                .firstNotNullOfOrNull { it[InvoiceEntries.fileName] }
        }
    }

    suspend fun deleteByFileName(fileName: String) = withContext(Dispatchers.IO) {
        transaction {
            InvoiceEntries.deleteWhere {
                InvoiceEntries.fileName eq fileName
            }
        }

        "Deleted invoice entry for fileName: $fileName".logger(Type.INFO)
    }
}
