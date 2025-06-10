package com.pavicontech.desktop.agent.presentation.helper

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.*
import java.net.URL
import java.net.URLConnection
import kotlin.math.roundToInt
import kotlin.system.measureTimeMillis

val logChannel = Channel<String>(Channel.UNLIMITED)

suspend fun installPdfCreator(onSuccess: suspend () -> Unit) {
    val installerUrl = "https://cdn.download.pdfforge.org/pdfcreator/6.0.1/PDFCreator-6_0_1-Setup.exe"
    val tempDir = System.getProperty("java.io.tmpdir")
    val installerFile = File(tempDir, "PDFCreatorInstaller.exe")

    log("📁 Temp Directory: $tempDir")
    log("⬇️ Downloading PDFCreator installer from: $installerUrl")

    val elapsedDownloadTime = measureTimeMillis {
        downloadWithProgress(installerUrl, installerFile)
    }

    log("✅ Installer downloaded to: ${installerFile.absolutePath}")
    log("⏱️ Download time: ${elapsedDownloadTime / 1000.0} seconds")

    log("🚀 Running silent installer...")

    val process = ProcessBuilder(
        installerFile.absolutePath,
        "/VERYSILENT",
        "/NORESTART"
    ).redirectErrorStream(true).start()

    withContext(Dispatchers.IO) {
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { log("[INSTALLER] $it") }
        }
    }

    val exitCode = process.waitFor()
    if (exitCode == 0) {
        log("🎉 PDFCreator installer exited successfully.")
        repeat(30) { attempt ->
            if (isPdfPrinterAvailable("PDFCreator")) {
                log("🖨️ Printer 'PDFCreator' is available.")
                launchPdfCreator()
                onSuccess()
                return
            }
            log("⏳ Waiting for PDFCreator printer to be ready... (Attempt ${attempt + 1}/30)")
            delay(1000)
        }
        log("❌ PDFCreator printer not found after waiting. Something went wrong.")
    } else {
        log("❌ Installation failed with exit code $exitCode")
    }
}

suspend fun downloadWithProgress(urlStr: String, outputFile: File) {
    val url = URL(urlStr)
    val connection: URLConnection = url.openConnection()
    val fileSize = connection.contentLengthLong

    BufferedInputStream(connection.getInputStream()).use { inputStream ->
        FileOutputStream(outputFile).use { outputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L
            val startTime = System.currentTimeMillis()

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead

                if (fileSize > 0 && (totalBytesRead % (1024 * 1024) < 8192 || totalBytesRead == fileSize)) {
                    val percent = (totalBytesRead * 100.0 / fileSize).roundToInt()
                    val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
                    val speed = if (elapsed > 0) totalBytesRead / 1024.0 / elapsed else 0.0
                    log("⬇️  Downloaded ${"%.2f".format(totalBytesRead / 1024.0 / 1024)} MB of ${"%.2f".format(fileSize / 1024.0 / 1024)} MB ($percent%) at ${"%.1f".format(speed)} KB/s")
                }
            }
            outputStream.flush()
        }
    }
}

suspend fun log(message: String) {
    logChannel.send("[${System.currentTimeMillis()}] $message")
}

fun isPdfPrinterAvailable(printerName: String): Boolean {
    return try {
        val process = ProcessBuilder("powershell", "-Command", "Get-Printer -Name '$printerName'")
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        output.contains(printerName, ignoreCase = true)
    } catch (e: Exception) {
        false
    }
}

suspend fun launchPdfCreator() {
    val pdfCreatorPath = "C:\\Program Files\\PDFCreator\\PDFCreator.exe"
    val pdfCreatorFile = File(pdfCreatorPath)

    if (pdfCreatorFile.exists()) {
        log("🚀 Launching PDFCreator...")
        val process = ProcessBuilder(pdfCreatorPath).start()

        withContext(Dispatchers.IO) {
            delay(5000) // Give it a few seconds to start (instead of waitFor with timeout)
        }

        log("✅ PDFCreator launched successfully.")
    } else {
        log("❌ PDFCreator executable not found at $pdfCreatorPath. Please check installation.")
    }
}
