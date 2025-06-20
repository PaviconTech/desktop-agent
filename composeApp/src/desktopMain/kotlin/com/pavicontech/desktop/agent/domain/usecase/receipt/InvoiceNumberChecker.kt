package com.pavicontech.desktop.agent.domain.usecase.receipt

import androidx.compose.runtime.mutableStateListOf
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class InvoiceEntry(
    val invoiceNumber: String,
    val fileName: String
)

class InvoiceNumberChecker(
    private val keyValueStorage: KeyValueStorage
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun doesInvoiceExist(invoiceNumber: String?): Boolean {
        if (invoiceNumber.isNullOrBlank()) return false

        val stored = keyValueStorage.get(Constants.INVOICE_NO_LIST)
        val list = try {
            stored?.let { Json.decodeFromString<List<InvoiceEntry>>(it) } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val result = list.any { it.invoiceNumber == invoiceNumber }
        "Invoice Number: $invoiceNumber, Exists: $result".logger(Type.INFO)
        return result
    }

    suspend fun addInvoice(invoiceNumber: String?, fileName: String) {
        if (invoiceNumber.isNullOrBlank()) return

        val stored = keyValueStorage.get(Constants.INVOICE_NO_LIST)
        val currentList = try {
            stored?.let { Json.decodeFromString<List<InvoiceEntry>>(it) }?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }

        if (currentList.any { it.invoiceNumber == invoiceNumber }) return

        currentList.add(InvoiceEntry(invoiceNumber, fileName))

        keyValueStorage.set(
            Constants.INVOICE_NO_LIST,
            Json.encodeToString(currentList)
        )
    }

    suspend fun getFileName(invoiceNumber: String?): String? {
        if (invoiceNumber.isNullOrBlank()) return null

        val stored = keyValueStorage.get(Constants.INVOICE_NO_LIST)
        val list = try {
            stored?.let { Json.decodeFromString<List<InvoiceEntry>>(it) } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return list.find { it.invoiceNumber == invoiceNumber }?.fileName
    }
}
