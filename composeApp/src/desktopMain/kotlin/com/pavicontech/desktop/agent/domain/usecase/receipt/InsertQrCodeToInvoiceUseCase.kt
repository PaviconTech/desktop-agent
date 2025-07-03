package com.pavicontech.desktop.agent.domain.usecase.receipt


import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.multipdf.PDFMergerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
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
import kotlin.math.max
import kotlin.math.min

class InsertQrCodeToInvoiceUseCase(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(
        inputPdf: File,
        outPutPdf: File,
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
                outputImage = outPutPdf
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
            document.save(outPutPdf)
            document.close()
            onSuccess()
        }
    }
}





suspend fun convertInvoicePdfToSingleImageWithQr(
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

    val font = Font("Helvetica", Font.BOLD, 20)
    val fontMetrics = Canvas().getFontMetrics(font)
    val lines = kraInfoText.split("\n")
    val lineHeight = fontMetrics.height
    val textBlockHeight = lines.size * lineHeight
    val padding = 20

    val qrSize = 200
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
}
