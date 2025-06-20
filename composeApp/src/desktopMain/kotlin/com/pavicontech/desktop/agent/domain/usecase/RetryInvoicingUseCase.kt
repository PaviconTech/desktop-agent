import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.Directory
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
import java.time.Instant

class RetryInvoicingUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val createSaleUseCase: CreateSaleUseCase,
    private val invoiceRepository: InvoiceRepository,
    private val extractInvoiceUseCase: ExtractInvoiceUseCase,
    private val printOutOptionUseCase: PrintOutOptionUseCase,
    private val keyValueStorage: KeyValueStorage,
    private val generateHtmlReceipt80MMUseCase: GenerateHtmlReceipt80MMUseCase,


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

        val fileName = fileData.fileName.replaceAfterLast('.', "pdf")
        println("Retry file: $fileName")
        val filePath = fileData.fullDirectory

        invoiceRepository.insertInvoice(
            Invoice(
                id = Instant.now().toEpochMilli().toString(),
                fileName = fileName,
                extractionStatus = ExtractionStatus.PENDING,
                etimsStatus = EtimsStatus.PENDING,
            )
        )

        extractInvoiceUseCase(
            filePath = filePath,
            fileName = fileName,
            businessInfo = businessInfo,
            onSuccess = { extractedData, saleItems, taxableAmount, _, invoiceItems, _ ->
                "items: $saleItems".logger(Type.INFO)

                val saleResult = createSaleUseCase.invoke(saleItems, taxableAmount)

                val updatedStatus = if (saleResult.status) EtimsStatus.SUCCESSFUL else EtimsStatus.FAILED
                val getPrintOutSize= keyValueStorage.get(Constants.PRINTOUT_SIZE)

                invoiceRepository.updateInvoice(
                    fileName = fileName,
                    invoice = Invoice(
                        invoiceNumber = extractedData.data?.invoiceNumber,
                        id = Instant.now().toEpochMilli().toString(),
                        fileName = if (getPrintOutSize == "80mm") fileName.replaceAfterLast('.', "png") else fileName,
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

    private suspend fun extractAndSubmitInvoice(fullPath: String, fileName: String) {

        pdfExtractorRepository.extractInvoiceData(
            InvoiceReq(file = fullPath.fileToByteArray(), fileName = fileName)
        )
    }
}
