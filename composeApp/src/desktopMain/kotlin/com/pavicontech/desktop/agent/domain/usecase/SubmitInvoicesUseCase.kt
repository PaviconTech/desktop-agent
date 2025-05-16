
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.fileToByteArray
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepository
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.ExtractionStatus
import com.pavicontech.desktop.agent.data.local.database.entries.Invoice
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.remote.dto.request.InvoiceReq
import com.pavicontech.desktop.agent.data.remote.dto.response.extractInvoice.ExtractInvoiceRes
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Instant

class SubmitInvoicesUseCase(
    private val pdfExtractorRepository: PDFExtractorRepository,
    private val directoryWatcherRepository: DirectoryWatcherRepository,
    private val invoiceRepository: InvoiceRepository,
    private val keyValueStorage: KeyValueStorage
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
            directoryWatcherRepository.watchDirectory(path = "/home/dev-pasaka/Desktop", onDelete = { dir ->

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
                                    businessInfo = bussinessInfo
                                )
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
        token: String, businessInfo: BusinessInformation, filePath: String, fileName: String, kraPin: String
    ) = withContext(Dispatchers.IO) {
        val result = invoiceRepository.insertInvoice(
            invoice = Invoice(
                fileName = fileName, kraPin = kraPin
            )
        )

        if (result) {
            "$fileName created successfully" logger (Type.INFO)
            extractInvoice(
                token = token, filePath = filePath, fileName = fileName, businessInfo = businessInfo
            )
        } else "$fileName creation failed"
    }

    private suspend fun extractInvoice(
        token: String,
        filePath: String,
        fileName: String,
        businessInfo: BusinessInformation,
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


}
