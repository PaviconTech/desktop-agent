package com.pavicontech.desktop.agent.domain.usecase

import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.domain.model.Invoice
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSalesUseCase(
    private val PDFExtractorRepository: PDFExtractorRepository
) {
    operator fun invoke(): Flow<Resource<List<Invoice>>> = flow {
        try {
            emit(Resource.Loading())
            val invoices = PDFExtractorRepository.getAllSales()
            if (invoices.status){
                emit(Resource.Success(data = invoices.toInvoices(), message = invoices.message))
            }else{
                emit(Resource.Error(message = invoices.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(message = e.message ?: "An unknown error occurred."))
        }
    }

}