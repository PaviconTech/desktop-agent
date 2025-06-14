package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.entries.Item
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class ExtractInvoiceUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val invoiceRepository: InvoiceRepository,
    private val localItemsRepository: ItemLocalRepository,
    ) {
     suspend  operator fun invoke(
        filePath: String,
        fileName: String,
        businessInfo: BusinessInformation,
        onSuccess: suspend (ExtractInvoiceRes, saleItems:List<CreateSaleItem>, taxableAmount:Int, fileName:String, invoiceItems:List<Item>, receiptType:String) -> Unit,
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val result = pdfExtractorRepository.extractInvoiceData(
                body = InvoiceReq(
                    fileName = fileName,
                    file = filePath.fileToByteArray()
                )
            )
            """
        --------------------------------------------------------------------------------------------------------------------------        
                Filename: ${result.data?.fileName}
                Customer Name: ${result.data?.customerName}
                Customer Pin: ${result.data?.customerPin}
                Items: ${result.data?.items}
                Amounts: ${result.data?.totals}
        -------------------------------------------------------------------------------------------------------------------------
            """.trimIndent().logger(Type.TRACE)
            if (result.status){
                invoiceRepository.updateInvoice(
                    fileName = fileName,
                    invoice = Invoice(
                        id = Instant.now().toEpochMilli().toString(),
                        fileName = fileName,
                        extractionStatus = ExtractionStatus.SUCCESSFUL,
                        etimsStatus = EtimsStatus.PENDING,
                        updatedAt = Instant.now().toString(),
                        items = result.data?.items?.map { it.toItem() } ?: emptyList()
                    )
                )
                onSuccess(
                    result,
                    filterItems(extractedItems = result.data?.items ?: emptyList()),
                    result.data?.totals?.subTotal?.toInt() ?: 0,
                    fileName,
                    result.data?.items?.map { it.toItem() } ?: emptyList(),
                    result.data?.documentType ?: "invoice"
                )
            }else{
                invoiceRepository.updateInvoice(
                    fileName = fileName,
                    invoice = Invoice(
                        id = Instant.now().toEpochMilli().toString(),
                        fileName = fileName,
                        extractionStatus = ExtractionStatus.FAILED,
                        etimsStatus = EtimsStatus.PENDING,
                        updatedAt = Instant.now().toString(),
                        items = result.data?.items?.map { it.toItem() } ?: emptyList()
                    )
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun filterItems(
        extractedItems: List<com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.Item>
    ): List<CreateSaleItem> {
        val storedItems = localItemsRepository.getAllItems()

        return extractedItems.mapNotNull { extracted ->
            val matchedStoredItem = storedItems.find {
                it.itemName.trim().equals(extracted.itemDescription.trim(), ignoreCase = true)
            }

            matchedStoredItem?.toCreateSaleItem(
                qty = extracted.quantity.toInt() ?: 1,
                prc = extracted.amount.toInt() ?: 0,
                dcRt =  0,
                dcAmt = 0,
                splyAmt = extracted.amount.toInt() ,
                taxblAmt = extracted.amount.toInt(),
                taxAmt = extracted.taxAmount?.toInt() ?: 0,
                totAmt = extracted.amount.toInt()
            )
        }
    }
}