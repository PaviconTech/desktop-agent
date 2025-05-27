package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class InsertQrCodeToInvoiceUseCase {
    suspend operator fun invoke(
        inputPdf: File,
        outPutPdf: File,
        qrCodeImage: File,
        kraInfoText: String,
        coordinates: List<BoxCoordinates>,
        onSuccess: suspend () -> Unit
    ): Unit = withContext(Dispatchers.IO) {
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
                contentStream.setFont(PDType1Font.HELVETICA, 12f) // adjust size as needed
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
