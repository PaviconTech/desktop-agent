package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.InvoiceNumberChecker

class DeleteInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceNumberChecker: InvoiceNumberChecker
) {
    suspend operator fun invoke(fileName: String, invoiceNumber: String?){
        try {
            invoiceRepository.deleteInvoiceByFileName(fileName)
            invoiceNumberChecker.doesInvoiceExist(invoiceNumber)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}