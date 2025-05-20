package com.pavicontech.desktop.agent.domain.usecase.receipt


import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths

class RenderAndSavePdfUseCase {

    /**
     * Renders the given HTML content into a PDF and saves it to the user's OS-specific documents directory.
     *
     * @param htmlContent The HTML content to render into PDF.
     * @param fileName The desired file name (e.g. "receipt.pdf"). The ".pdf" extension will be enforced.
     * @return The full path of the saved PDF file.
     */
    operator fun invoke(htmlContent: String, fileName: String): File {
        val sanitizedFileName = if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf"
        val outputFile = getDefaultSavePath(sanitizedFileName)

        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }

        FileOutputStream(outputFile).use { outputStream ->
            val renderer = PdfRendererBuilder()
            renderer.withHtmlContent(htmlContent, null)
            renderer.toStream(outputStream)
            renderer.run()
        }

        return outputFile
    }

    private fun getDefaultSavePath(fileName: String): File {
        val userHome = System.getProperty("user.home")
        val documentsDir = when {
            isWindows() -> Paths.get(userHome, "Documents", "Receipts")
            isMac() -> Paths.get(userHome, "Documents", "Receipts")
            isLinux() -> Paths.get(userHome, "Documents", "Receipts")
            else -> Paths.get(userHome, "Receipts")
        }

        return documentsDir.resolve(fileName).toFile()
    }

    private fun isWindows() = System.getProperty("os.name").lowercase().contains("win")
    private fun isMac() = System.getProperty("os.name").lowercase().contains("mac")
    private fun isLinux() = System.getProperty("os.name").lowercase().contains("nux")
}
