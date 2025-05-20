package com.pavicontech.desktop.agent.domain.usecase.receipt

import qrcode.QRCode
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
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
        width: Int = 300,
        height: Int = 300
    ): File {
        val logoPath = "/logo-img.png"
        val logoBytes = loadLogoAsByteArray(logoPath, width = width, height=height)

        val qrCode = QRCode.Companion
            .ofRoundedSquares()
            .withColor(0xFF886034.toInt())
            .withLogo(logoBytes, width, height, false)
            .withSize(25)
            .build("https://etims.pavicontech.com/sales/$randomData")

        val pngBytes = qrCode.render()

        // Save QR to filesystem
        val qrFile = path.toFile()
        qrFile.parentFile.mkdirs() // Ensure directories exist

        FileOutputStream(qrFile).use { it.write(pngBytes.getBytes()) }

        return qrFile
    }
/*

    private fun getQrCodeFilePath(): File {
        val userHome = System.getProperty("user.home")
        val path = Paths.get(userHome, "Documents", "Receipts", "qr-code.png")
        return path.toFile()
    }
*/

    private fun loadLogoAsByteArray(logoPath: String, width: Int, height: Int): ByteArray {
        val inputStream = this::class.java.getResourceAsStream(logoPath)
            ?: throw IllegalArgumentException("Logo file not found: $logoPath")

        val image = ImageIO.read(inputStream)
        val resized = resizeImage(image, width, height)
        val circular = addCircularBackground(resized, width + 4)

        val baos = ByteArrayOutputStream()
        ImageIO.write(circular, "PNG", baos)
        return baos.toByteArray()
    }

    private fun resizeImage(image: BufferedImage, width: Int, height: Int): BufferedImage {
        val resizedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = resizedImage.createGraphics()
        graphics.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null)
        graphics.dispose()
        return resizedImage
    }

    private fun addCircularBackground(logo: BufferedImage, size: Int): BufferedImage {
        val circular = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val graphics = circular.createGraphics()
        graphics.color = Color.WHITE
        graphics.fillOval(0, 0, size, size)
        val x = (size - logo.width) / 2
        val y = (size - logo.height) / 2
        graphics.drawImage(logo, x, y, null)
        graphics.dispose()
        return circular
    }
}