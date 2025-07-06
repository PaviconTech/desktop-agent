package com.pavicontech.desktop.agent.domain.usecase.sales

import SaveHtmlAsPdfUseCase
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
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
import com.pavicontech.desktop.agent.domain.usecase.receipt.InvoiceNumberChecker
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import java.lang.System
import java.nio.file.Paths
import java.time.Instant
import javax.imageio.ImageIO
import kotlin.io.path.pathString

class ExtractInvoiceUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val invoiceRepository: InvoiceRepository,
    private val localItemsRepository: ItemLocalRepository,
    private val keyValueStorage: KeyValueStorage,
    private val printReceiptUseCase: PrintReceiptUseCase,
    private val invoiceNumberChecker: InvoiceNumberChecker,
) {
    suspend operator fun invoke(
        filePath: String,
        fileName: String,
        businessInfo: BusinessInformation,
        onSuccess: suspend (ExtractInvoiceRes, saleItems: List<CreateSaleItem>, taxableAmount: Int, fileName: String, invoiceItems: List<Item>, receiptType: String) -> Unit,
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val printerName = keyValueStorage.get(Constants.SELECTED_PRINTER)?.trim()
            "Selected printer: '$printerName'".logger(Type.INFO)
            val getInvoiceWords = keyValueStorage.get(Constants.INVOICE_NO_PREFIX)
            val getPrintOutSize = keyValueStorage.get(Constants.PRINTOUT_SIZE)
            val extractionResult = pdfExtractorRepository.extractInvoiceData(
                body = InvoiceReq(
                    fileName = fileName,
                    file = filePath.fileToByteArray(),
                    invoiceWords = getInvoiceWords
                )
            )
            """
        --------------------------------------------------------------------------------------------------------------------------        
                Filename: ${extractionResult.data?.fileName}
                Invoice Number: ${extractionResult.data?.invoiceNumber}
                Customer Name: ${extractionResult.data?.customerName}
                Customer Pin: ${extractionResult.data?.customerPin}
                Items: ${extractionResult.data?.items}
                Amounts: ${extractionResult.data?.totals}
        -------------------------------------------------------------------------------------------------------------------------
            """.trimIndent().logger(Type.TRACE)

            val doesInvoiceExist =
                invoiceRepository.getInvoicesByInvoiceNumber(extractionResult.data?.invoiceNumber ?: "")

            "Invoices available from db : ${doesInvoiceExist.size}".logger(Type.INFO)

            if (invoiceNumberChecker.doesInvoiceExist(extractionResult.data?.invoiceNumber)) {
                val home = System.getProperty("user.home")
                val path = Paths.get(home, "Documents", "DesktopAgent", "FiscalizedReceipts")
                val filename = if (getPrintOutSize == "80mm")
                    invoiceNumberChecker.getFileName(extractionResult.data?.invoiceNumber)?.replaceAfterLast('.', "png")
                else fileName
                val image =
                    File(path.pathString, filename)
                if (getPrintOutSize == "80mm") {
                    loadImageFromFile(image)?.let {
                        SaveHtmlAsPdfUseCase().printImageFromBufferedImage(
                            image =it,
                            printerName = printerName ?: ""
                        )
                    }

                    return@withContext
                } else {
                    printReceiptUseCase.invoke(
                        filePath = "${path.toFile().path}/${
                            invoiceNumberChecker.getFileName(
                                extractionResult.data?.invoiceNumber
                            )
                        }"
                    )
                    return@withContext
                }

            } else {

                if (extractionResult.status) {
                    invoiceRepository.insertInvoice(
                        invoice = Invoice(
                            invoiceNumber = extractionResult.data?.invoiceNumber,
                            id = Instant.now().toEpochMilli().toString(),
                            fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast(
                                '.',
                                "png"
                            ) else fileName,
                            extractionStatus = ExtractionStatus.SUCCESSFUL,
                            etimsStatus = null,
                            updatedAt = Instant.now().toString(),
                            items = extractionResult.data?.items?.map { it.toItem() } ?: emptyList()
                        )
                    )

                    onSuccess(
                        extractionResult,
                        filterItems(extractedItems = extractionResult.data?.items ?: emptyList()),
                        extractionResult.data?.totals?.subTotal?.toInt() ?: 0,
                        fileName,
                        extractionResult.data?.items?.map { it.toItem() } ?: emptyList(),
                        extractionResult.data?.documentType ?: "invoice"
                    )
                } else {
                    invoiceRepository.insertInvoice(
                        invoice = Invoice(
                            id = Instant.now().toEpochMilli().toString(),
                            fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast(
                                '.',
                                "png"
                            ) else fileName,
                            extractionStatus = ExtractionStatus.FAILED,
                            etimsStatus = EtimsStatus.PENDING,
                            updatedAt = Instant.now().toString(),
                            items = extractionResult.data?.items?.map { it.toItem() } ?: emptyList()
                        )
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun filterItems(
        extractedItems: List<com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.Item>
    ): List<CreateSaleItem> {
        val storedItems = localItemsRepository.getAllItems()

        val items =  extractedItems.mapNotNull { extracted ->
            val matchedStoredItem = storedItems.find {
                it.itemName.trim().equals(extracted.itemDescription.trim(), ignoreCase = true)
            }

            matchedStoredItem?.toCreateSaleItem(
                qty = extracted.quantity.toInt() ?: 1,
                prc = extracted.amount.toInt() ?: 0,
                dcRt = 0,
                dcAmt = 0,
                splyAmt = extracted.amount.toInt(),
                taxblAmt = extracted.amount.toInt(),
                taxAmt = extracted.taxAmount?.toInt() ?: 0,
                totAmt = extracted.amount.toInt()
            )
        }
        "Sending items for sale: $items".logger(Type.INFO)

        return  items
    }


    fun loadImageFromFile(file: File): BufferedImage? {
        return try {
            ImageIO.read(file)
        } catch (e: Exception) {
            println("Failed to load image from file: ${file.path}")
            e.printStackTrace()
            null
        }
    }
}