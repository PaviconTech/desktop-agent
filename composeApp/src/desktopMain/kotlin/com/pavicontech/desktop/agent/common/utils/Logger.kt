package com.pavicontech.desktop.agent.common.utils

enum class Type(val colorCode: String) {
    INFO("\u001B[32m"),   // Green
    WARN("\u001B[33m"),   // Yellow
    DEBUG("\u001B[34m"),  // Blue
    TRACE("\u001B[31m")   // Red
}

private const val RESET = "\u001B[0m"

infix fun String.logger(type: Type) {
    val timestamp = java.time.LocalDateTime.now()
    val coloredMessage = "${type.colorCode}[$timestamp] [$type] $this$RESET"
    println(coloredMessage)
}
