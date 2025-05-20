import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.FilesystemRepository
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

class SubmitInvoicesUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val filesystemRepository: FilesystemRepository,
    private val invoiceRepository: InvoiceRepository,
    private val keyValueStorage: KeyValueStorage,
    private val renderAndSavePdfUseCase: RenderAndSavePdfUseCase,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val printReceiptUseCase: PrintReceiptUseCase,
    private val generateQrCodeAndKraInfoUseCase: GenerateQrCodeAndKraInfoUseCase,
    private val insertQrCodeToInvoiceUseCase: InsertQrCodeToInvoiceUseCase
) {

    suspend operator fun invoke(): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""
            val bussinessInfo = try {
                val json = keyValueStorage.get(Constants.BUSINESS_INFORMATION) ?: ""
                Json.decodeFromString<BusinessInformation>(json)
            } catch (e: Exception) {
                null
            }
            filesystemRepository.watchDirectory(path = "/home/dev-pasaka/Desktop", onDelete = { dir ->

            }, onModify = { dir ->
                bussinessInfo?.let {
                    pdfExtractorRepository.extractInvoiceData(
                        body = it.toInvoiceReq().copy(
                            file = dir.fullDirectory.fileToByteArray(), fileName = dir.fileName
                        ), token = token
                    )
                }
            }

            ).collect { event ->
                when (event) {
                    is Resource.Loading -> {
                        "Event: ${event.message}  ${event.data} \n" logger (Type.INFO)
                    }

                    is Resource.Success -> {
                        //  "Event: ${event.message}  ${event.data} \n" logger (Type.INFO)
                        launch {
                            bussinessInfo?.let {
                                createInvoice(
                                    token = token,
                                    filePath = event.data?.fullDirectory ?: "",
                                    fileName = event.data?.fileName ?: "",
                                    kraPin = bussinessInfo.kraPin,
                                    businessInfo = bussinessInfo,
                                    onSuccess = { extractedDataRes ->
                                        launch {
                                            val htmlContent = generateHtmlReceipt.invoke(
                                                data = extractedDataRes.toExtractedData(), businessInfo = bussinessInfo
                                            )
                                            printOutOption(
                                                htmlContent = htmlContent,
                                                fileName = event.data?.fileName ?: "",
                                                filePath = event.data?.fullDirectory ?: ""
                                            )
                                        }

                                    })
                            }
                        }
                    }

                    is Resource.Error -> {
                        println("Event: ${event.message}  ${event.data} \n")
                    }
                }
            }
        } catch (e: Exception) {


        }
    }

    private suspend fun createInvoice(
        token: String,
        businessInfo: BusinessInformation,
        filePath: String,
        fileName: String,
        kraPin: String,
        onSuccess: suspend (ExtractInvoiceRes) -> Unit
    ) = withContext(Dispatchers.IO) {
        val result = invoiceRepository.insertInvoice(
            invoice = Invoice(
                fileName = fileName, kraPin = kraPin
            )
        )

        if (result) {
            "$fileName created successfully" logger (Type.INFO)
            extractInvoice(
                token = token,
                filePath = filePath,
                fileName = fileName,
                businessInfo = businessInfo,
                onSuccess = onSuccess
            )
        } else "$fileName creation failed"
    }

    private suspend fun extractInvoice(
        token: String,
        filePath: String,
        fileName: String,
        businessInfo: BusinessInformation,
        onSuccess: suspend (ExtractInvoiceRes) -> Unit
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val result = pdfExtractorRepository.extractInvoiceData(
                body = InvoiceReq(
                    id = businessInfo.id,
                    name = businessInfo.name,
                    branchId = businessInfo.branchId,
                    branchName = businessInfo.branchName,
                    districtName = businessInfo.districtName,
                    kraPin = businessInfo.kraPin,
                    provinceName = businessInfo.provinceName,
                    sectorName = businessInfo.sectorName,
                    sdcId = businessInfo.sdcId,
                    taxpayerName = businessInfo.taxpayerName,
                    file = filePath.fileToByteArray()
                ), token = token
            )
            """
        --------------------------------------------------------------------------------------------------------------------------        
                Filename: ${result.data?.fileName}
                Items: ${result.data?.items}
                Amounts: ${result.data?.totals}
        -------------------------------------------------------------------------------------------------------------------------
            """.trimIndent().logger(Type.TRACE)
            if (result.status) {
                invoiceRepository.updateInvoice(
                    fileName = fileName, invoice = result.toInvoice().copy(
                        extractionStatus = ExtractionStatus.SUCCESSFUL, updatedAt = Instant.now().toString()
                    )
                )
                onSuccess(result)
                //"$fileName extracted successfully" logger (Type.INFO)
            } else {
                invoiceRepository.updateInvoice(
                    fileName = fileName, invoice = result.toInvoice().copy(
                        extractionStatus = ExtractionStatus.SUCCESSFUL, updatedAt = Instant.now().toString()
                    )
                )
                "$fileName extraction failed" logger (Type.INFO)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun printOutOption(
        htmlContent: String,
        fileName: String,
        filePath: String
    ) {
        val option = keyValueStorage.get(Constants.PRINT_OUT_OPTIONS) ?: "default"
        val qrCodeCoordinates = BoxCoordinates.fromJson(
            keyValueStorage.get(Constants.QR_CODE_COORDINATES) ?: ""
        )
        val kraInfoCoordinates = BoxCoordinates.fromJson(
            keyValueStorage.get(Constants.KRA_INFO_COORDINATES) ?: ""
        )

        when (option) {
            "default" -> {
                val userHome = System.getProperty("user.home")
                val path = Paths.get(userHome, "Documents", "DesktopAgent", "Receipts", fileName)
                renderAndSavePdfUseCase.invoke(htmlContent = htmlContent, fileName = fileName)
                printReceiptUseCase.invoke(filePath = path.pathString)
            }

            else -> {
                val userHome = System.getProperty("user.home")
                val path = Paths.get(userHome, "Documents", "DesktopAgent", "FiscalizedReceipts", fileName)
                Paths.get(userHome, "Documents", "DesktopAgent", "FiscalizedReceipts").toFile().mkdirs()
                generateQrCodeAndKraInfoUseCase(
                    fileNamePrefix = fileName,
                    internalReferenceNumber = "Test1234567",
                    receiptSignature = "Receipt12345",
                    vsdcDate = Instant.now().toString(),
                    mrcNumber = "MRC1235678",
                    onSuccess = { qrCode, kraInfo ->
                        filePath.logger(Type.DEBUG)
                        val inPutPdf = Path(filePath).toFile()
                        insertQrCodeToInvoiceUseCase.invoke(
                            inputPdf = inPutPdf,
                            outPutPdf = path.toFile(),
                            qrCodeImage = qrCode,
                            kraInfoImage = kraInfo,
                            coordinates = listOf(kraInfoCoordinates, qrCodeCoordinates),
                            onSuccess = {
                                qrCode.delete()
                                kraInfo.delete()
                            }
                        )

                    },
                    onCleanUp = {_,_ ->

                    }
                )

            }
        }

    }


}
