package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.image.BufferedImage
import java.awt.print.PrinterJob
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies
import javax.print.attribute.standard.MediaPrintableArea
import javax.print.attribute.standard.OrientationRequested

class PrintReceiptUseCase(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(filePath: String): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext "File not found." to false

        try {
            val printerName = keyValueStorage.get(Constants.SELECTED_PRINTER)?.trim()
            "Selected printer: '$printerName'".logger(Type.INFO)

            val printServices = PrinterJob.lookupPrintServices()
            val selectedPrinter = printServices.find {
                it.name.trim().equals(printerName, ignoreCase = true)
            }

            if (selectedPrinter == null) {
                "Printer '$printerName' not found.".logger(Type.WARN)
                return@withContext "Printer not found." to false
            }

            return@withContext when {
                filePath.endsWith(".pdf", ignoreCase = true) -> {
                    val document = PDDocument.load(file)
                    val job = PrinterJob.getPrinterJob()
                    job.printService = selectedPrinter
                    job.setPageable(PDFPageable(document))
                    job.print()
                    document.close()
                    "PDF printed successfully." to true
                }

                filePath.endsWith(".png", ignoreCase = true) || filePath.endsWith(".jpg", ignoreCase = true) -> {
                    val image = ImageIO.read(file)
                    if (image == null) return@withContext "Could not read image file." to false
                    val scaledImage = resizeImage(image, 640) // scale to 80mm width @203 DPI
                    printImageFromBufferedImage(scaledImage, printerName ?: "")
                    "Image sent to printer successfully." to true
                }

                else -> "Unsupported file format." to false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Failed to print: ${e.message}" to false
        }
    }
}

fun printImageFromBufferedImage(image: BufferedImage, printerName: String) {
    val outputStream = ByteArrayOutputStream()
    ImageIO.write(image, "png", outputStream)
    val inputStream = ByteArrayInputStream(outputStream.toByteArray())

    val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
        add(Copies(1))
        add(OrientationRequested.PORTRAIT)
        add(MediaPrintableArea(0f, 0f, 80f, 297f, MediaPrintableArea.MM))
    }

    val printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PNG, null)
    val selectedPrinter = printServices.find { it.name.equals(printerName, ignoreCase = true) }

    if (selectedPrinter == null) {
        println("Printer '$printerName' not found.")
        return
    }

    val docPrintJob = selectedPrinter.createPrintJob()
    val doc = SimpleDoc(inputStream, DocFlavor.INPUT_STREAM.PNG, null)

    try {
        docPrintJob.print(doc, printRequestAttributeSet)
        println("Printing started on '$printerName'")
    } catch (e: PrintException) {
        println("Failed to print: ${e.message}")
        e.printStackTrace()
    }
}

fun resizeImage(input: BufferedImage, targetWidth: Int): BufferedImage {
    val scale = targetWidth.toDouble() / input.width
    val newHeight = (input.height * scale).toInt()

    val resized = BufferedImage(targetWidth, newHeight, BufferedImage.TYPE_INT_RGB)
    val g = resized.createGraphics()
    g.drawImage(input, 0, 0, targetWidth, newHeight, null)
    g.dispose()

    return resized
}
