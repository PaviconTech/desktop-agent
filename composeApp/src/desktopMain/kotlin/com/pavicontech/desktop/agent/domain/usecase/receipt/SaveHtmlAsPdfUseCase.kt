import gui.ava.html.image.generator.HtmlImageGenerator
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies
import javax.print.attribute.standard.MediaSizeName
import javax.print.attribute.standard.OrientationRequested

class SaveHtmlAsPdfUseCase() {

    operator fun invoke(html: String, outputFile: File, printerName: String) {
        outputFile.parentFile?.mkdirs()

        val generator = HtmlImageGenerator()
        generator.loadHtml(html)

        // Save image in-memory first to adjust contrast
        val bufferedImage = generator.getBufferedImage()

        // Convert to black & white (monochrome) to improve thermal printing
        val bwImage = BufferedImage(bufferedImage.width, bufferedImage.height, BufferedImage.TYPE_BYTE_BINARY)
        val g = bwImage.createGraphics()
        g.drawImage(bufferedImage, 0, 0, null)
        g.dispose()

        // Write black & white image to file
        ImageIO.write(bwImage, "png", outputFile)

        // Print the high-contrast image
        printImage(outputFile.absolutePath, printerName)
    }

    fun printImage(filePath: String, printerName: String) {
        val file = File(filePath)
        if (!file.exists()) {
            println("File not found: $filePath")
            return
        }

        println("Printing receipt: ${file.path}")

        val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
            add(Copies(1))
            add(MediaSizeName.ISO_A4) // You can adjust to your actual paper size
            add(OrientationRequested.PORTRAIT)
        }

        val printServices = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.PNG, null)
        val selectedPrinter = printServices.find { it.name.equals(printerName, ignoreCase = true) }

        if (selectedPrinter == null) {
            println("Printer '$printerName' not found.")
            return
        }

        val docPrintJob = selectedPrinter.createPrintJob()
        val doc = SimpleDoc(file.inputStream(), DocFlavor.INPUT_STREAM.PNG, null)

        try {
            docPrintJob.print(doc, printRequestAttributeSet)
            println("Printing started on '$printerName'")
        } catch (e: PrintException) {
            println("Failed to print: ${e.message}")
            e.printStackTrace()
        }
    }
}
