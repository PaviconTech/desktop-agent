package com.pavicontech.desktop.agent.common.utils

import java.io.*
import java.nio.file.Paths
import java.time.LocalDateTime

object LogManager {

    private val logFile: File by lazy {
        val home = System.getProperty("user.home")
        val path = Paths.get(home, "Documents", "DesktopAgent" )
        val dir = File(path.toFile(), ".etimsSync/logs")
        dir.mkdirs()
        File(dir, "app.log")
    }

    private lateinit var logWriter: PrintWriter

    // Expose the log file for the LogsScreen
    fun retrieveLogFile(): File = logFile

    // Log level methods
    fun logInfo(message: String) {
        log("INFO", message)
    }

    fun logWarning(message: String) {
        log("WARNING", message)
    }

    fun logError(message: String) {
        log("ERROR", message)
    }

    fun logDebug(message: String) {
        log("DEBUG", message)
    }

    private fun log(level: String, message: String) {
        println("[${LocalDateTime.now()}] [$level] $message")
    }

    fun init() {
        try {
            logWriter = PrintWriter(FileWriter(logFile, true), true)

            // Redirect stdout and stderr
            val combinedStream = DualOutputStream(System.out, logWriter)
            System.setOut(PrintStream(combinedStream, true))

            val errorStream = DualOutputStream(System.err, logWriter)
            System.setErr(PrintStream(errorStream, true))

            // Handle uncaught exceptions
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                val msg = "[${LocalDateTime.now()}] [UNCAUGHT-EXCEPTION] in ${thread.name}: ${throwable.stackTraceToString()}"
                println(msg) // this also goes to logFile due to redirect
            }

            println("[${LocalDateTime.now()}] [LOG INIT] Logging started.")

        } catch (e: Exception) {
            println("Failed to initialize logging: ${e.message}")
        }
    }

    private class DualOutputStream(
        private val console: OutputStream,
        private val file: PrintWriter
    ) : OutputStream() {
        private val buffer = ByteArrayOutputStream()

        override fun write(b: Int) {
            buffer.write(b)
            if (b == '\n'.code) {
                flush()
            }
        }

        override fun flush() {
            val line = buffer.toString("UTF-8")
            buffer.reset()
            console.write(line.toByteArray())
            console.flush()
            file.println(line.trim())
        }
    }
}
