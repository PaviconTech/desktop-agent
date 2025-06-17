package com.pavicontech.desktop.agent.common.utils

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Paths
import java.time.LocalDateTime

enum class Type(val colorCode: String) {
    INFO("\u001B[32m"),   // Green
    WARN("\u001B[33m"),   // Yellow
    DEBUG("\u001B[34m"),  // Blue
    TRACE("\u001B[31m")   // Red
}

private const val RESET = "\u001B[0m"

// Define your log file location (user home directory is safe on all platforms)
private val logFile: File by lazy {
    val home = System.getProperty("user.home")
    val path = Paths.get(home, "Documents", "DesktopAgent" )
    val dir = File(path.toFile(), ".coinx/logs")
    dir.mkdirs()
    File(dir, "app.log")
}

infix fun String.logger(type: Type) {
    val timestamp = LocalDateTime.now()
    val logLine = "[$timestamp] [$type] $this"

    // Console with color
    val coloredMessage = "${type.colorCode}$logLine$RESET"
    println(coloredMessage)

    // Append to log file
    try {
        PrintWriter(FileWriter(logFile, true)).use { out ->
            out.println(logLine) // No color codes in file
        }
    } catch (e: Exception) {
        println("Failed to write log to file: ${e.message}")
    }
}

