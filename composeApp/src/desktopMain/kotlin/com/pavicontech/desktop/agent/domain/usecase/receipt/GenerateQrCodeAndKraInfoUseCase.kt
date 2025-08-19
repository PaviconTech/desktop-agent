package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO


import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.rendering.PDFRenderer


class GenerateQrCodeAndKraInfoUseCase(
    private val generateQrCodeImage: GenerateQrCodeUseCase,
    private val keyValueStorage: KeyValueStorage
) {
    private val userHome = System.getProperty("user.home")
    private val path = Paths.get(userHome, "Documents", "DesktopAgent", "KraInfo")

    suspend operator fun invoke(
        fileNamePrefix: String,
        businessPin: String,
        bhfId: String,
        rcptSign:String,
        qrUrl: String,
        onSuccess: suspend (qrCodeFile: File) -> Unit,
        onCleanUp: (qrCodeFile: File) -> Unit
    ) {
        val qrCodeSize = BoxCoordinates.fromJson(
            keyValueStorage.get(Constants.QR_CODE_COORDINATES) ?: ""
        )
        val kraInfoSize = BoxCoordinates.fromJson(
            keyValueStorage.get(Constants.KRA_INFO_COORDINATES) ?: ""
        )

        path.toFile().mkdirs()




        val qrCodeFile = File(path.toFile(), "$fileNamePrefix-qr.png")
        generateQrCodeImage.invoke(
            path = qrCodeFile.toPath(),
            data = qrUrl
        )
        try {
            onSuccess(qrCodeFile)
        } finally {
            onCleanUp(qrCodeFile)
        }
    }

}
