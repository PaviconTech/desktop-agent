package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.Result
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.pathString
import SaveHtmlAsPdfUseCase


class PrintOutOptionUseCase(
    private val keyValueStorage: KeyValueStorage,
    private val renderAndSavePdfUseCase: RenderAndSavePdfUseCase,
    private val printReceiptUseCase: PrintReceiptUseCase,
    private val generateQrCodeAndKraInfoUseCase: GenerateQrCodeAndKraInfoUseCase,
    private val insertQrCodeToInvoiceUseCase: InsertQrCodeToInvoiceUseCase,

    ) {


    suspend operator fun invoke(
        htmlContent: String,
        htmlContent80mm: String,
        fileName: String,
        filePath: String,
        kraResult: Result,
        businessInfo: BusinessInformation
    ) {
        val option = keyValueStorage.get(Constants.PRINT_OUT_OPTIONS) ?: "default"
        val qrCodeCoordinates = BoxCoordinates.Companion.fromJson(
            keyValueStorage.get(Constants.QR_CODE_COORDINATES) ?: ""
        )
        val kraInfoCoordinates = BoxCoordinates.Companion.fromJson(
            keyValueStorage.get(Constants.KRA_INFO_COORDINATES) ?: ""
        )
        val printOutStatus = keyValueStorage.get(Constants.PRINTOUT_SIZE)
        val printerName = keyValueStorage.get(Constants.SELECTED_PRINTER)?.trim() ?: ""

        val userHome = System.getProperty("user.home")
        val receiptDir = Paths.get(userHome, "Documents", "DesktopAgent", "FiscalizedReceipts")
        receiptDir.toFile().mkdirs() // ✅ Ensure directory exists
        val path = receiptDir.resolve(
            if (printOutStatus == "80mm") fileName.replaceAfterLast('.', "png") else fileName
        )

//        if (printOutStatus == "80mm"){
//            "printer name $printerName".logger(Type.INFO)
//            SaveHtmlAsPdfUseCase().invoke(
//                html = htmlContent80mm,
//                outputFile = path.toFile(),
//                printerName=printerName
//            )
//            return Unit
//        }

        when (option) {
            "default" -> {
                renderAndSavePdfUseCase.invoke(htmlContent = htmlContent, fileName = fileName)
                printReceiptUseCase.invoke(filePath = path.pathString)

            }

            else -> {
                generateQrCodeAndKraInfoUseCase(
                    fileNamePrefix = fileName,
                    businessPin = businessInfo.kraPin,
                    bhfId = businessInfo.branchId,
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
                                printReceiptUseCase.invoke(path.pathString)
                                qrCode.delete()
                            }
                        )
                    },
                    onCleanUp = { _ -> }
                )
            }
        }
    }




}