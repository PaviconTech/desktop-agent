package com.pavicontech.desktop.agent.domain.usecase

import RetryInvoicingUseCase
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.Directory
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import io.github.vinceglb.filekit.utils.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path

class AutoRetryUseCase(
    private val invoiceLocalRepository: InvoiceRepository,
    private val keyValueStorage: KeyValueStorage,
    private val retryInvoicingUseCase: RetryInvoicingUseCase
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val invoices = invoiceLocalRepository.getAllInvoices()
        val failedInvoices = invoices.filter { it.etimsStatus == EtimsStatus.FAILED }
        val watchFolder = keyValueStorage.get(Constants.WATCH_FOLDER)

        failedInvoices.forEach { invoice ->
            "Retrying invoice: ${invoice.fileName}".logger(Type.WARN)
            val path = watchFolder?.let { Path(it, invoice.fileName) }
            val dir = Directory(
                fullDirectory = path?.toFile()?.path ?: "",
                path = watchFolder ?: "",
                fileName = invoice.fileName
            )
            retryInvoicingUseCase(
                file = dir,
                onError = {},
                isLoading = {},
                onSuccess = {}
            )

        }

    }
}