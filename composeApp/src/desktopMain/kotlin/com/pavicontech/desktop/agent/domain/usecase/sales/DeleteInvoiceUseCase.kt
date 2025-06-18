package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository

class DeleteInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(fileName: String){
        try {
            invoiceRepository.deleteInvoiceByFileName(fileName)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}