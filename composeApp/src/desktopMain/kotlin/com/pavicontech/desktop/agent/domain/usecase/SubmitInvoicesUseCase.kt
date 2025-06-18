import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.Directory
import com.pavicontech.desktop.agent.data.fileSystem.FilesystemRepository
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt80MMUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.ExtractInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintOutOptionUseCase
import kotlinx.coroutines.*
import java.io.File
import java.time.Instant
import kotlin.io.path.pathString

class SubmitInvoicesUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val filesystemRepository: FilesystemRepository,
    private val keyValueStorage: KeyValueStorage,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val generateHtmlReceipt80MMUseCase: GenerateHtmlReceipt80MMUseCase,
    private val createSaleUseCase: CreateSaleUseCase,
    private val invoiceRepository: InvoiceRepository,
    private val extractInvoiceUseCase: ExtractInvoiceUseCase,
    private val printOutOptionUseCase: PrintOutOptionUseCase
) {

    suspend operator fun invoke(): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        try {
            val businessInfo = keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.fromBusinessJson()
            val watchDir = keyValueStorage.get(Constants.WATCH_FOLDER)
            if (watchDir == null){
                val file = File(Constants.INVOICE_WATCH_FOLDER.pathString)
                file.mkdirs()
                keyValueStorage.set(Constants.WATCH_FOLDER, Constants.INVOICE_WATCH_FOLDER.pathString)
            }
            val watchFolder = keyValueStorage.get(Constants.WATCH_FOLDER) ?: ""

            filesystemRepository.watchDirectory(
                path = watchFolder,
                onDelete = {}, // No-op for now
                onModify = { file ->
                    businessInfo?.let {
                        handleSuccess(file, it)
                    }
                }
            ).collect { event ->
               businessInfo?.let {
                   handleEvent(event = event, businessInfo = it)
               }
            }

        } catch (e: Exception) {
            "Error watching invoices: ${e.message}".logger(Type.WARN)
        }
    }

    private fun CoroutineScope.handleEvent(event: Resource<Directory>,businessInfo: BusinessInformation) {
        when (event) {
            is Resource.Loading -> {
                "Loading: ${event.message}".logger(Type.INFO)
            }

            is Resource.Success -> {
                launch { handleSuccess(event.data, businessInfo) }
            }

            is Resource.Error -> {
                "Error: ${event.message}".logger(Type.WARN)
            }
        }
    }

    private suspend fun handleSuccess(fileData: Directory?, businessInfo: BusinessInformation) {
        if (fileData == null) return

        val fileName = fileData.fileName
        val filePath = fileData.fullDirectory



        extractInvoiceUseCase(
            filePath = filePath,
            fileName = fileName,
            businessInfo = businessInfo,
            onSuccess = { extractedData, saleItems, taxableAmount, _, invoiceItems, _ ->

                val saleResult = createSaleUseCase.invoke(saleItems, taxableAmount,
                    customerName = extractedData.data?.customerName,
                    customerPin = extractedData.data?.customerPin)

                val updatedStatus = if (saleResult.status) EtimsStatus.SUCCESSFUL else EtimsStatus.FAILED

                invoiceRepository.updateInvoice(
                    fileName = fileName,
                    invoice = Invoice(
                        invoiceNumber = extractedData.data?.invoiceNumber,
                        id = Instant.now().toEpochMilli().toString(),
                        fileName = fileName,
                        extractionStatus = ExtractionStatus.SUCCESSFUL,
                        etimsStatus = updatedStatus,
                        items = invoiceItems
                    )
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
                        rcptSign = saleResult.kraResult?.result?.rcptSign ?: "No Receipt Sign",
                        kraResult = saleResult.kraResult?.result,

                        )

                    saleResult.kraResult?.result?.let {
                        printOutOptionUseCase(
                            kraResult = it,
                            htmlContent = htmlContent,
                            htmlContent80mm = htmlContent80mm,
                            fileName = fileName,
                            filePath = filePath,
                            businessInfo = businessInfo
                        )
                    }
                }
            }
        )
    }

    private suspend fun extractAndSubmitInvoice(fullPath: String, fileName: String) {

        pdfExtractorRepository.extractInvoiceData(
            InvoiceReq(file = fullPath.fileToByteArray(), fileName = fileName)
        )
    }
}
