package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class InsertQrCodeToInvoiceUseCase() {
    suspend operator fun invoke(
        inputPdf: File,
        outPutPdf: File,
        qrCodeImage: File,
        kraInfoImage: File,
        coordinates: List<BoxCoordinates>,
        onSuccess: () -> Unit
    ): Unit = withContext(Dispatchers.IO) {
        val document = PDDocument.load(inputPdf)
        val page: PDPage = document.getPage(0)
        val mediaBox: PDRectangle = page.mediaBox

        val image1Buffered: BufferedImage = ImageIO.read(qrCodeImage)
        val image2Buffered: BufferedImage = ImageIO.read(kraInfoImage)

        val pdImage1 = LosslessFactory.createFromImage(document, image1Buffered)
        val pdImage2 = LosslessFactory.createFromImage(document, image2Buffered)

        val contentStream = org.apache.pdfbox.pdmodel.PDPageContentStream(
            document,
            page,
            org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND,
            true,
            true
        )

        coordinates.forEachIndexed { index, coord ->
            val x = coord.startX
            val y = mediaBox.height - coord.endY // PDF y=0 is at bottom, so we flip it
            val width = coord.endX-coord.startX
            val height = coord.endY - coord.startY

            val image = if (index == 0) pdImage1 else pdImage2
            contentStream.drawImage(image, x, y, width, height)
        }

        contentStream.close()
        document.save(outPutPdf)
        document.close()
        onSuccess()
    }
}