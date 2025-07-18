import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.Directory
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.entries.Invoices.fileName
import com.pavicontech.desktop.agent.data.local.database.entries.Item
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt80MMUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InvoiceNumberChecker
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.ExtractInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintOutOptionUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.LevenshteinDistance
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import java.time.Instant
import javax.imageio.ImageIO
import kotlin.io.path.pathString
import kotlin.jvm.functions.FunctionN

class RetryInvoicingUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val createSaleUseCase: CreateSaleUseCase,
    private val invoiceRepository: InvoiceRepository,
    private val printOutOptionUseCase: PrintOutOptionUseCase,
    private val keyValueStorage: KeyValueStorage,
    private val generateHtmlReceipt80MMUseCase: GenerateHtmlReceipt80MMUseCase,
    private val invoiceNumberChecker: InvoiceNumberChecker,
    private val localItemsRepository: ItemLocalRepository



    ) {



    suspend operator fun invoke(
        file:Directory,
        isLoading: (Boolean) -> Unit,
        onSuccess: (Boolean) -> Unit,
        onError: (Boolean) -> Unit
    ): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        println(file.fileName)
        val businessInfo = keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.fromBusinessJson()

        try {
            isLoading(true)
            businessInfo?.let{
                handleSuccess(file, it)
                onSuccess(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onError(true)
            "Error watching invoices: ${e.message}".logger(Type.WARN)
        }
        isLoading(false)
    }


    private suspend fun handleSuccess(fileData: Directory?, businessInfo: BusinessInformation) {
        if (fileData == null) return


        val getPrintOutSize = keyValueStorage.get(Constants.PRINTOUT_SIZE)

        val del = if (getPrintOutSize == "80mm") fileData.fileName.replaceAfterLast('.', "png")
        else fileData.fileName.replaceAfterLast('.', "pdf")
        invoiceNumberChecker.deleteByFileName(del)

        val fileName = fileData.fileName.replaceAfterLast('.', "pdf")
        println("Retry file: $fileName")
        val filePath = fileData.fullDirectory.replaceAfterLast('.', "pdf")


        retryExtractInvoice(
            filePath = filePath,
            fileName = fileName,
            businessInfo = businessInfo,
            onSuccess = { extractedData, saleItems, taxableAmount, _, invoiceItems, _ ->
                "items: $saleItems".logger(Type.INFO)

                val saleResult = createSaleUseCase.invoke(
                    items = saleItems,
                    taxableAmount = taxableAmount,
                    customerName = extractedData.data?.customerName,
                    customerPin = extractedData.data?.customerPin
                    )

                val updatedStatus = if (saleResult.status) EtimsStatus.SUCCESSFUL else EtimsStatus.FAILED
                val getPrintOutSize= keyValueStorage.get(Constants.PRINTOUT_SIZE)

                invoiceRepository.updateInvoice(
                    fileName = if (getPrintOutSize == "80mm")
                        fileName.replaceAfterLast('.', "png")
                    else fileName,
                    invoice = Invoice(
                        invoiceNumber = extractedData.data?.invoiceNumber,
                        id = Instant.now().toEpochMilli().toString(),
                        fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast('.', "png") else fileName,
                        extractionStatus = ExtractionStatus.SUCCESSFUL,
                        etimsStatus = updatedStatus,
                        items = invoiceItems
                    )
                )

                invoiceNumberChecker.addInvoice(
                    invoiceNumber = extractedData.data?.invoiceNumber,
                    fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast('.', "png") else fileName
                )

                if (saleResult.status) {
                    val htmlContent = generateHtmlReceipt(
                        data = extractedData.toExtractedData(),
                        businessInfo = businessInfo,
                        businessPin = businessInfo.kraPin,
                        bhfId = businessInfo.branchId,
                        rcptSign = saleResult.kraResult?.result?.rcptSign ?: "No Receipt Sign"
                    )
                    val htmlContent80mm = generateHtmlReceipt80MMUseCase(
                        data = extractedData.toExtractedData(),
                        businessInfo = businessInfo,
                        businessPin = businessInfo.kraPin,
                        bhfId = businessInfo.branchId,
                        kraResult = saleResult.kraResult?.result,
                        rcptSign = saleResult.kraResult?.result?.rcptSign ?: "No Receipt Sign"
                    )

                    saleResult.kraResult?.result?.let {
                        printOutOptionUseCase(
                            kraResult = it,
                            htmlContent80mm = htmlContent80mm,
                            htmlContent = htmlContent,
                            fileName = fileName,
                            filePath = filePath,
                            businessInfo = businessInfo,
                        )
                    }
                }
            }
        )
    }



    private suspend fun retryExtractInvoice(
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
                    invoiceWords = getInvoiceWords,
                    bussinessPin = businessInfo.kraPin
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


                if (extractionResult.status) {
                    invoiceRepository.updateInvoice(
                        fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast(
                            '.',
                            "png"
                        ) else fileName,
                        invoice = Invoice(
                            invoiceNumber = extractionResult.data?.invoiceNumber,
                            id = Instant.now().toEpochMilli().toString(),
                            fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast(
                                '.',
                                "png"
                            ) else fileName,
                            extractionStatus = ExtractionStatus.SUCCESSFUL,
                            etimsStatus = EtimsStatus.PENDING,
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
                    invoiceRepository.updateInvoice(
                        fileName = fileName,
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




  /*  private suspend fun filterItems(
        extractedItems: List<com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.Item>
    ): List<CreateSaleItem> {
        val storedItems = localItemsRepository.getAllItems()


        val items =  extractedItems.mapNotNull { extracted ->
            val matchedStoredItem = storedItems.find {
                it.itemName.trim().equals(extracted.itemDescription.trim(), ignoreCase = true)
            }

            val taxAmount = when(extracted.taxType){
                "A" -> 0
                "C" -> 0
                "E" -> {
                    val amount = extracted.amount.toInt() * extracted.quantity.toInt()
                    ((0.08) * amount).toInt()
                }
                "B" -> {
                    val amount = extracted.amount.toInt() * extracted.quantity.toInt()
                    ((0.16) * amount).toInt()
                }
                "D" -> 0

                else -> 0
            }
            val itemAmount = extracted.amount.toInt() * extracted.quantity.toInt()
            matchedStoredItem?.toCreateSaleItem(
                qty = extracted.quantity.toInt(),
                prc = extracted.amount.toInt(),
                dcRt = 0,
                dcAmt = 0,
                splyAmt = itemAmount,
                taxblAmt = itemAmount-taxAmount,
                taxAmt = taxAmount,
                totAmt = itemAmount
            )
        }
        "Sending items for sale: $items".logger(Type.INFO)
        "Items: $items".logger(Type.DEBUG)

        return  items

    }*/




    private suspend fun filterItems(
        extractedItems: List<com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.Item>
    ): List<CreateSaleItem> {
        val storedItems = localItemsRepository.getAllItems()
        val levenshtein = LevenshteinDistance(3) // Set threshold (3 is usually good)

        val items = extractedItems.mapNotNull { extracted ->
            val cleanedExtracted = extracted.itemDescription.trim().lowercase()

            // 1. Try exact match
            var matchedStoredItem = storedItems.find {
                it.itemName.trim().equals(cleanedExtracted, ignoreCase = true)
            }

            // 2. If no exact match, try fuzzy match
            if (matchedStoredItem == null) {
                matchedStoredItem = storedItems.minByOrNull {
                    levenshtein.apply(cleanedExtracted, it.itemName.trim().lowercase())
                }?.takeIf {
                    levenshtein.apply(cleanedExtracted, it.itemName.trim().lowercase()) <= 3
                }

                if (matchedStoredItem != null) {
                    "🟡 Fuzzy matched '${extracted.itemDescription}' to '${matchedStoredItem.itemName}'".logger(Type.WARN)
                } else {
                    "❌ Could not match item '${extracted.itemDescription}'".logger(Type.WARN)
                }
            }else {
                "Item Matched: ${matchedStoredItem.itemName}".logger(Type.INFO)
            }

            val taxAmount = when (extracted.taxType) {
                "A", "C", "D" -> 0
                "E" -> ((0.08) * extracted.amount.toInt() * extracted.quantity.toInt()).toInt()
                "B" -> ((0.16) * extracted.amount.toInt() * extracted.quantity.toInt()).toInt()
                else -> 0
            }

            val itemAmount = extracted.amount.toInt() * extracted.quantity.toInt()

            matchedStoredItem?.toCreateSaleItem(
                qty = extracted.quantity.toInt(),
                prc = extracted.amount.toInt(),
                dcRt = 0,
                dcAmt = 0,
                splyAmt = itemAmount,
                taxblAmt = itemAmount - taxAmount,
                taxAmt = taxAmount,
                totAmt = itemAmount
            )
        }

        "✅ Sending ${items.size} items for sale.".logger(Type.INFO)
        return items
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
