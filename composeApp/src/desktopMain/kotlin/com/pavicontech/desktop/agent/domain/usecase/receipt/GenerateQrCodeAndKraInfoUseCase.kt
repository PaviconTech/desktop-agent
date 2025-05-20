package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import java.awt.*
import java.awt.image.BufferedImage
import java.io.*
import java.nio.file.Paths
import javax.imageio.ImageIO

class GenerateQrCodeAndKraInfoUseCase(
    private val generateQrCodeImage: GenerateQrCodeUseCase,
    private val keyValueStorage: KeyValueStorage
) {
    private val userHome = System.getProperty("user.home")
    private val path = Paths.get(userHome, "Documents", "DesktopAgent", "KraInfo")

    suspend operator fun invoke(
        fileNamePrefix: String,
        internalReferenceNumber: String,
        receiptSignature: String,
        vsdcDate: String,
        mrcNumber: String,
        onSuccess: suspend (qrCodeFile:File, kraInfoFile:File) -> Unit,
        onCleanUp: (qrCodeFile:File, kraInfoFile:File) -> Unit
    ) {
        val qrCodeSize = BoxCoordinates.fromJson(keyValueStorage.get(Constants.QR_CODE_COORDINATES) ?: "")
        val kraInfo = BoxCoordinates.fromJson(keyValueStorage.get(Constants.KRA_INFO_COORDINATES) ?: "")

        // Ensure folder exists
        path.toFile().mkdirs()

        val receiptText = """
            Internal Reference: $internalReferenceNumber
            Receipt Signature: $receiptSignature
            VSDC Date: $vsdcDate
            MRC Number: $mrcNumber

            This is a computer-generated receipt.
            No signature required • Demo TaxPoint
        """.trimIndent()

        // 1. Save QR code
        val qrCodeFile = File(path.toFile(), "$fileNamePrefix-qr.png")
        generateQrCodeImage(
            internalReferenceNumber,
            qrCodeFile.toPath(),
            width = (qrCodeSize.endX-qrCodeSize.startX).toInt(),
            height = (qrCodeSize.endY-qrCodeSize.startY).toInt()
        )

        // 2. Save KRA Info as image
        val kraInfoFile = File(path.toFile(), "$fileNamePrefix-kra.png")
        saveTextAsImage(
            receiptText,
            kraInfoFile,
            width = (qrCodeSize.endX-qrCodeSize.startX).toInt(),
        )
        try {
            onSuccess(qrCodeFile, kraInfoFile)
        } finally {
            onCleanUp(qrCodeFile, kraInfoFile)
        }
    }

    private fun saveTextAsImage(text: String, outputFile: File, width: Int = 400, fontSize: Int = 14) {
        val lines = text.split("\n")

        val font = Font("SansSerif", Font.PLAIN, fontSize)
        val lineHeight = fontSize + 6
        val padding = 20
        val height = padding * 2 + lines.size * lineHeight

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()

        g.color = Color.WHITE
        g.fillRect(0, 0, width, height)

        g.color = Color(0x4A, 0x55, 0x68) // Text color
        g.font = font

        var y = padding + lineHeight
        for (line in lines) {
            g.drawString(line, padding, y)
            y += lineHeight
        }

        g.dispose()
        ImageIO.write(image, "png", outputFile)
    }
}
