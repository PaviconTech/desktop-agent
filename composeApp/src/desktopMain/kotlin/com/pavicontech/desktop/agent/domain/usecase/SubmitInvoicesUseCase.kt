import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.FilesystemRepository
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.EtimsStatus
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.entries.Item
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createSale.CreateSaleItem
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.KraResult
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.Result
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.ExtractedItem
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.pathString

class SubmitInvoicesUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val filesystemRepository: FilesystemRepository,
    private val keyValueStorage: KeyValueStorage,
    private val renderAndSavePdfUseCase: RenderAndSavePdfUseCase,
    private val generateHtmlReceipt: GenerateHtmlReceipt,
    private val printReceiptUseCase: PrintReceiptUseCase,
    private val generateQrCodeAndKraInfoUseCase: GenerateQrCodeAndKraInfoUseCase,
    private val insertQrCodeToInvoiceUseCase: InsertQrCodeToInvoiceUseCase,
    private val createSaleUseCase: CreateSaleUseCase,
    private val invoiceRepository: InvoiceRepository
) {

    suspend operator fun invoke(): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""

            val userHome = System.getProperty("user.home")
            val path = Paths.get(userHome, "Documents")
            val watchDir = keyValueStorage.get(Constants.WATCH_FOLDER) ?: path.pathString

            val bussinessInfo =BusinessInformation(
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
            filesystemRepository.watchDirectory(path = watchDir, onDelete = { dir ->

            }, onModify = { dir ->
                bussinessInfo?.let {
                    pdfExtractorRepository.extractInvoiceData(
                        body = InvoiceReq(
                            file = dir.fullDirectory.fileToByteArray(),
                            fileName = dir.fileName
                        ),
                    )
                }
            }

            ).collect { event ->
                event.toString().logger(Type.INFO)
                when (event) {
                    is Resource.Loading -> {
                        "Event: ${event.message}  ${event.data} \n" logger (Type.INFO)
                    }

                    is Resource.Success -> {
                        "Event: ${event.message}  ${event.data} \n" logger (Type.INFO)
                        launch {
                            invoiceRepository.insertInvoice(
                                Invoice(
                                    id = Instant.now().toEpochMilli().toString(),
                                    fileName = event.data?.fileName ?: "",
                                    extractionStatus = ExtractionStatus.PENDING,
                                    etimsStatus = EtimsStatus.PENDING,
                                )
                            )
                            extractInvoice(
                                filePath = event.data?.fullDirectory ?: "",
                                fileName = event.data?.fileName ?: "",
                                onSuccess = { extractedDataRes, items, taxableAmount, fileName, invoiceItems ->
                                    launch {
                                        val result = createSaleUseCase.invoke(
                                            items = items,
                                            taxableAmount = taxableAmount
                                        ).also {
                                            if (it.status) {
                                                invoiceRepository.updateInvoice(
                                                    fileName = fileName,
                                                    invoice = Invoice(
                                                        id = Instant.now().toEpochMilli().toString(),
                                                        fileName = fileName,
                                                        extractionStatus = ExtractionStatus.SUCCESSFUL,
                                                        etimsStatus = EtimsStatus.SUCCESSFUL,
                                                        items = invoiceItems
                                                    )
                                                )
                                            }else{
                                                invoiceRepository.updateInvoice(
                                                    fileName = fileName,
                                                    invoice = Invoice(
                                                        id = Instant.now().toEpochMilli().toString(),
                                                        fileName = fileName,
                                                        extractionStatus = ExtractionStatus.SUCCESSFUL,
                                                        etimsStatus = EtimsStatus.FAILED,
                                                        items = invoiceItems
                                                    )
                                                )
                                            }
                                        }
                                        if (result.status){
                                            val htmlContent = generateHtmlReceipt.invoke(
                                                data = extractedDataRes.toExtractedData(),
                                                businessInfo = bussinessInfo,
                                                businessPin = "P051901215P",
                                                bhfId = "00",
                                                rcptSign = result.kraResult.result.rcptSign
                                            )

                                            printOutOption(
                                                kraResult = result.kraResult.result,
                                                htmlContent = htmlContent,
                                                fileName = event.data?.fileName ?: "",
                                                filePath = event.data?.fullDirectory ?: ""
                                            )
                                        }
                                    }

                                },
                            )
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


    private suspend fun extractInvoice(
        filePath: String,
        fileName: String,
        onSuccess: suspend (ExtractInvoiceRes,List<CreateSaleItem>, taxableAmount:Int, fileName:String, invoiceItems:List<Item>) -> Unit,
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
                    result.data?.items?.map { it.toItem() } ?: emptyList()
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

    private suspend fun printOutOption(
        htmlContent: String,
        fileName: String,
        filePath: String,
        kraResult: Result
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
                    businessPin = "P051901215P",
                    bhfId = "00",
                    rcptSign = kraResult.rcptSign,
                    onSuccess = { qrCode ->
                        filePath.logger(Type.DEBUG)
                        val inPutPdf = Path(filePath).toFile()
                        val receiptText = """
                            INTRLDATA: ${kraResult.intrlData}
                            RCPTSIGN: ${kraResult.rcptSign}
                            VSDC DATE: ${kraResult.vsdcRcptPbctDate}
                            MRCNO: ${kraResult.mrcNo},
                            """.trimIndent()
                        insertQrCodeToInvoiceUseCase.invoke(
                            kraInfoText = receiptText,
                            inputPdf = inPutPdf,
                            outPutPdf = path.toFile(),
                            qrCodeImage = qrCode,
                            coordinates = listOf(kraInfoCoordinates, qrCodeCoordinates),
                            onSuccess = {
                               // printReceiptUseCase.invoke(path.pathString)
                                qrCode.delete()
                            }
                        )

                    },
                    onCleanUp = { _ ->

                    }
                )

            }
        }

    }


    suspend fun filterItems(
        extractedItems: List<com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.Item>
    ): List<CreateSaleItem> {
        val storedItems = keyValueStorage.get(Constants.ITEM_LIST)?.let {
            Json.decodeFromString<List<com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item>>(it)
        } ?: emptyList()

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
