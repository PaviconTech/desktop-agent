package com.pavicontech.desktop.agent.common.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateTimestamp(): String {
    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    return now.format(formatter)
}