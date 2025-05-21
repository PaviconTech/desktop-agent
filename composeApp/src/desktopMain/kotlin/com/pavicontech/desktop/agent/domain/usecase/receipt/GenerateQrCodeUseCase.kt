package com.pavicontech.desktop.agent.domain.usecase.receipt

import qrcode.QRCode
import qrcode.render.QRCodeGraphics
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

class GenerateQrCodeUseCase {

    operator fun invoke(
        randomData: String,
        path:Path,
        width: Int,
        height:Int
    ): File {
        val logoPath = "/logo-img.png"

        val qrCode = QRCode.Companion
            .ofRoundedSquares()
            .withColor(0xFF000000.toInt())
            .withSize(25)
            .build("https://etims.pavicontech.com/sales/$randomData")

        val pngBytes = qrCode
            .render()

        // Save QR to filesystem
        val qrFile = path.toFile()
        qrFile.parentFile.mkdirs() // Ensure directories exist

        FileOutputStream(qrFile).use { it.write(pngBytes.getBytes()) }

        return qrFile
    }


}