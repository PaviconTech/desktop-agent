package com.pavicontech.desktop.agent.common.utils

import java.io.File


fun String.fileToByteArray(): ByteArray {
    val file = File(this)
    return file.readBytes()
}