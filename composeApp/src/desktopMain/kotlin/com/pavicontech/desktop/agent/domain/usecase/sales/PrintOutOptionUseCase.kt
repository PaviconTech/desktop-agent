package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.Result
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.pathString

class PrintOutOptionUseCase(
    private val keyValueStorage: KeyValueStorage,
    private val renderAndSavePdfUseCase: RenderAndSavePdfUseCase,
    private val printReceiptUseCase: PrintReceiptUseCase,
    private val generateQrCodeAndKraInfoUseCase: GenerateQrCodeAndKraInfoUseCase,
    private val insertQrCodeToInvoiceUseCase: InsertQrCodeToInvoiceUseCase,

    ) {


    suspend operator fun invoke(
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
                val path = Paths.get(userHome, "Documents", "DesktopAgent", "FiscalizedReceipts", fileName)
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
                                printReceiptUseCase.invoke(path.pathString)
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

}