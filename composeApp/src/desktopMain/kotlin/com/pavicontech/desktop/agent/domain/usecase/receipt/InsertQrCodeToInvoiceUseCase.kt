package com.pavicontech.desktop.agent.domain.usecase.receipt


import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.rendering.PDFRenderer
import java.awt.Canvas
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class InsertQrCodeToInvoiceUseCase(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(
        inputPdf: File,
        outPutImage: File,
        qrCodeImage: File,
        kraInfoText: String,
        coordinates: List<BoxCoordinates>,
        onSuccess: suspend () -> Unit
    ): Unit = withContext(Dispatchers.IO) {

        val printOutSize = keyValueStorage.get(Constants.PRINTOUT_SIZE)

        if (printOutSize == "80mm") {
            convertInvoicePdfToSingleImageWithQr(
                inputPdf = inputPdf,
                qrImageFile = qrCodeImage,
                kraInfoText = kraInfoText,
                outputImage = outPutImage
            )
            onSuccess()
            return@withContext

        } else {

            val document = PDDocument.load(inputPdf)
            val page: PDPage = document.getPage(0)
            val mediaBox: PDRectangle = page.mediaBox
            val pageWidth = mediaBox.width
            val pageHeight = mediaBox.height

            val image1Buffered: BufferedImage = ImageIO.read(qrCodeImage)

            val pdImage1 = LosslessFactory.createFromImage(document, image1Buffered)

            val contentStream = org.apache.pdfbox.pdmodel.PDPageContentStream(
                document,
                page,
                org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND,
                true,
                true
            )

            val scaleX = pageWidth / 595f
            val scaleY = pageHeight / 842f

            coordinates.forEachIndexed { index, coord ->
                val x = coord.startX * scaleX
                val y = (842 - coord.endY) * scaleY  // Flip Y from top-left to bottom-left
                val width = (coord.endX - coord.startX) * scaleX
                val height = (coord.endY - coord.startY) * scaleY

                if (index == 0) {
                    contentStream.drawImage(pdImage1, x, y, width, height)
                } else {

                    // Text insertion
                    contentStream.beginText()
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8f) // adjust size as needed
                    contentStream.newLineAtOffset(x, y + height - 12f) // adjust Y so it starts from top
                    kraInfoText.split("\n").forEach { line ->
                        contentStream.showText(line)
                        contentStream.newLineAtOffset(0f, -10f) // line spacing
                    }
                    contentStream.endText()
                }
            }

            contentStream.close()
            document.save(outPutImage)
            document.close()
            onSuccess()
        }
    }
}





/*suspend fun convertInvoicePdfToSingleImageWithQr(
    inputPdf: File,
    qrImageFile: File,
    kraInfoText: String,
    outputImage: File
) = withContext(Dispatchers.IO) {
    val document = PDDocument.load(inputPdf)
    val renderer = PDFRenderer(document)
    val invoiceImage = renderer.renderImageWithDPI(0, 203f) // 203dpi common for thermal printers
    document.close()

    val qrImage = ImageIO.read(qrImageFile)

    val font = Font("Helvetica", Font.BOLD, 30)
    val fontMetrics = Canvas().getFontMetrics(font)
    val lines = kraInfoText.split("\n")
    val lineHeight = fontMetrics.height
    val textBlockHeight = lines.size * lineHeight
    val padding = 50

    val qrSize = 500
    val totalHeight = invoiceImage.height + qrSize + textBlockHeight + (2 * padding)
    val totalWidth: Int = invoiceImage.width

    val finalImage = BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB)
    val g = finalImage.createGraphics()

    // White background
    g.color = Color.WHITE
    g.fillRect(0, 0, totalWidth, totalHeight)

    // Draw invoice
    g.drawImage(invoiceImage, 0, 0, null)

    // Draw QR
    g.drawImage(qrImage.getScaledInstance(qrSize, qrSize, Image.SCALE_SMOOTH), padding, invoiceImage.height + padding, null)

    // Draw KRA text below QR
    g.color = Color.BLACK
    g.font = font
    val textStartY = invoiceImage.height + padding + qrSize + padding
    lines.forEachIndexed { i, line ->
        g.drawString(line, padding, textStartY + (i * lineHeight))
    }

    g.dispose()

    ImageIO.write(finalImage, "png", outputImage)
}*/


/*
suspend fun convertInvoicePdfToSingleImageWithQr(
    inputPdf: File,
    qrImageFile: File,
    kraInfoText: String,
    outputImage: File
) = withContext(Dispatchers.IO) {
    val document = PDDocument.load(inputPdf)
    val renderer = PDFRenderer(document)
    val invoiceImage = renderer.renderImageWithDPI(0, 203f) // full page
    document.close()

    // --- Crop white space ---
    val croppedInvoice = cropWhiteSpace(invoiceImage)

    val qrImage = ImageIO.read(qrImageFile)

    val font = Font("Helvetica", Font.BOLD, 30)
    val fontMetrics = Canvas().getFontMetrics(font)
    val lines = kraInfoText.split("\n")
    val lineHeight = fontMetrics.height
    val textBlockHeight = lines.size * lineHeight
    val padding = 50

    val qrSize = 400
    val totalHeight = croppedInvoice.height + qrSize + textBlockHeight + (2 * padding)
    val totalWidth: Int = croppedInvoice.width

    val finalImage = BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB)
    val g = finalImage.createGraphics()

    // White background
    g.color = Color.WHITE
    g.fillRect(0, 0, totalWidth, totalHeight)

    // Draw cropped invoice (no huge bottom space)
    g.drawImage(croppedInvoice, 0, 0, null)

    // Draw QR
    g.drawImage(qrImage.getScaledInstance(qrSize, qrSize, Image.SCALE_SMOOTH), padding, croppedInvoice.height + padding, null)

    // Draw KRA text below QR
    g.color = Color.BLACK
    g.font = font
    val textStartY = croppedInvoice.height + padding + qrSize + padding
    lines.forEachIndexed { i, line ->
        g.drawString(line, padding, textStartY + (i * lineHeight))
    }

    g.dispose()
    ImageIO.write(finalImage, "png", outputImage)
}

*/
/**
 * Crop empty (white) space around an image.
 *//*

fun cropWhiteSpace(source: BufferedImage): BufferedImage {
    val width = source.width
    val height = source.height

    var top = 0
    var bottom = height - 1
    var left = 0
    var right = width - 1

    // Find top boundary
    loop@ for (y in 0 until height) {
        for (x in 0 until width) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                top = y
                break@loop
            }
        }
    }

    // Find bottom boundary
    loop@ for (y in height - 1 downTo 0) {
        for (x in 0 until width) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                bottom = y
                break@loop
            }
        }
    }

    // Find left boundary
    loop@ for (x in 0 until width) {
        for (y in 0 until height) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                left = x
                break@loop
            }
        }
    }

    // Find right boundary
    loop@ for (x in width - 1 downTo 0) {
        for (y in 0 until height) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                right = x
                break@loop
            }
        }
    }

    val newWidth = right - left + 1
    val newHeight = bottom - top + 1

    return source.getSubimage(left, top, newWidth, newHeight)
}
*/


suspend fun convertInvoicePdfToSingleImageWithQr(
    inputPdf: File,
    qrImageFile: File,
    kraInfoText: String,
    outputImage: File
) = withContext(Dispatchers.IO) {
    val document = PDDocument.load(inputPdf)
    val renderer = PDFRenderer(document)
    val invoiceImage = renderer.renderImageWithDPI(0, 203f) // full page at 203 dpi
    document.close()

    // --- Crop white space with margin ---
    val croppedInvoice = cropWhiteSpace(invoiceImage, margin = 20)

    // --- Resize to thermal printer width (≈ 83mm ~ 680px at 203dpi) ---
    val targetWidth = 760
    val scale = targetWidth.toDouble() / croppedInvoice.width
    val targetHeight = (croppedInvoice.height * scale).toInt()

    val resizedInvoice = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
    val g2 = resizedInvoice.createGraphics()
    g2.color = Color.WHITE
    g2.fillRect(0, 0, targetWidth, targetHeight)
    g2.drawImage(croppedInvoice, 0, 0, targetWidth, targetHeight, null)
    g2.dispose()

    val qrImage = ImageIO.read(qrImageFile)

    val font = Font("Helvetica", Font.BOLD, 30)
    val fontMetrics = Canvas().getFontMetrics(font)
    val lines = kraInfoText.split("\n")
    val lineHeight = fontMetrics.height
    val textBlockHeight = lines.size * lineHeight
    val padding = 50

    // Scale QR to about half the receipt width
    val qrSize = (targetWidth * 0.5).toInt()

    val totalHeight = resizedInvoice.height + qrSize + textBlockHeight + (2 * padding)
    val totalWidth = resizedInvoice.width

    val finalImage = BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB)
    val g = finalImage.createGraphics()

    // White background
    g.color = Color.WHITE
    g.fillRect(0, 0, totalWidth, totalHeight)

    // Draw resized invoice (no extra bottom space)
    g.drawImage(resizedInvoice, 0, 0, null)

    // Draw QR centered
    val qrX = (totalWidth - qrSize) / 2
    g.drawImage(qrImage.getScaledInstance(qrSize, qrSize, Image.SCALE_SMOOTH), qrX, resizedInvoice.height + padding, null)

    // Draw KRA text below QR
    g.color = Color.BLACK
    g.font = font
    val textStartY = resizedInvoice.height + padding + qrSize + padding
    lines.forEachIndexed { i, line ->
        g.drawString(line, padding, textStartY + (i * lineHeight))
    }

    g.dispose()
    ImageIO.write(finalImage, "png", outputImage)
}

/**
 * Crop empty (white) space around an image, keeping a margin.
 */
fun cropWhiteSpace(source: BufferedImage, margin: Int = 25): BufferedImage {
    val width = source.width
    val height = source.height

    var top = 0
    var bottom = height - 1
    var left = 0
    var right = width - 1

    // Find top boundary
    loop@ for (y in 0 until height) {
        for (x in 0 until width) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                top = y
                break@loop
            }
        }
    }

    // Find bottom boundary
    loop@ for (y in height - 1 downTo 0) {
        for (x in 0 until width) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                bottom = y
                break@loop
            }
        }
    }

    // Find left boundary
    loop@ for (x in 0 until width) {
        for (y in 0 until height) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                left = x
                break@loop
            }
        }
    }

    // Find right boundary
    loop@ for (x in width - 1 downTo 0) {
        for (y in 0 until height) {
            if (Color(source.getRGB(x, y)) != Color.WHITE) {
                right = x
                break@loop
            }
        }
    }

    // Add safe margin
    val newTop = (top - margin).coerceAtLeast(0)
    val newBottom = (bottom + margin).coerceAtMost(height - 1)
    val newLeft = (left - margin).coerceAtLeast(0)
    val newRight = (right + margin).coerceAtMost(width - 1)

    val newWidth = newRight - newLeft + 1
    val newHeight = newBottom - newTop + 1

    return source.getSubimage(newLeft, newTop, newWidth, newHeight)
}

