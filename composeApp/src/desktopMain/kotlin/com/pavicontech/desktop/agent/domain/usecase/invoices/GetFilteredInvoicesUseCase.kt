package com.pavicontech.desktop.agent.domain.usecase.invoices

import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.isActive
import kotlinx.coroutines.isActive

class GetFilteredInvoicesUseCase(
    private val invoiceLocalRepository: InvoiceRepository
) {
    operator fun invoke(filter: String): Flow<List<Invoice>> = flow {
        while (currentCoroutineContext().isActive) {
            val invoices = invoiceLocalRepository.getAllInvoices()

            val extractionStatus = when (filter) {
                "EXTRACTION_PENDING" -> ExtractionStatus.PENDING
                "EXTRACTION_FAILED" -> ExtractionStatus.FAILED
                "EXTRACTION_SUCCESSFUL" -> ExtractionStatus.SUCCESSFUL
                else -> null
            }

            val etimsStatus = when (filter) {
                "ETIMS_PENDING" -> EtimsStatus.PENDING
                "ETIMS_FAILED" -> EtimsStatus.FAILED
                "ETIMS_SUCCESSFUL" -> EtimsStatus.SUCCESSFUL
                else -> null
            }

            emit(invoices.filter {
                (extractionStatus == null || it.extractionStatus == extractionStatus) &&
                        (etimsStatus == null || it.etimsStatus == etimsStatus)
            })
            delay(3000)

        }
    }
}
