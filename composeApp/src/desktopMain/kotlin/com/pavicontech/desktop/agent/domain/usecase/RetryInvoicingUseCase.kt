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
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.ExtractInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.PrintOutOptionUseCase
import kotlinx.coroutines.*
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.pathString

class RetryInvoicingUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val createSaleUseCase: CreateSaleUseCase,
    private val invoiceRepository: InvoiceRepository,
    private val extractInvoiceUseCase: ExtractInvoiceUseCase,
    private val printOutOptionUseCase: PrintOutOptionUseCase
) {

    private val businessInfo = BusinessInformation(
        id = 23456,
        branchId = "12345678",
        branchName = "Demo12",
        districtName = "Kasarani",
        kraPin = "P000000000D",
        name = "John Doe",
        provinceName = "Kasarani",
        sectorName = "Agritech",
        sdcId = "123456",
        taxpayerName = "John Doe",
        businessLogo = "P00000000D",
    )

    suspend operator fun invoke(
        file:Directory,
        isLoading: (Boolean) -> Unit,
        onSuccess: (Boolean) -> Unit,
        onError: (Boolean) -> Unit
    ): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        try {
            isLoading(true)
            handleSuccess(file)
            onSuccess(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onError(true)
            "Error watching invoices: ${e.message}".logger(Type.WARN)
        }
        isLoading(false)
    }


    private suspend fun handleSuccess(fileData: Directory?) {
        if (fileData == null) return

        val fileName = fileData.fileName
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
            onSuccess = { extractedData, saleItems, taxableAmount, _, invoiceItems, _ ->

                val saleResult = createSaleUseCase.invoke(saleItems, taxableAmount)

                val updatedStatus = if (saleResult.status) EtimsStatus.SUCCESSFUL else EtimsStatus.FAILED

                invoiceRepository.updateInvoice(
                    fileName = fileName,
                    invoice = Invoice(
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
                        businessPin = "P051901215P",
                        bhfId = "00",
                        rcptSign = saleResult.kraResult?.result?.rcptSign ?: "No Receipt Sign"
                    )

                    saleResult.kraResult?.result?.let {
                        printOutOptionUseCase(
                            kraResult = it,
                            htmlContent = htmlContent,
                            fileName = fileName,
                            filePath = filePath
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
