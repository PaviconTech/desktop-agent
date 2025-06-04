package com.pavicontech.desktop.agent.domain.usecase.sales

import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import qrcode.QRCode

class GenerateQRBitmap() {
    @OptIn(ExperimentalResourceApi::class)
    operator fun invoke(
        qrData: String
    ): ImageBitmap {
        val qrCode = QRCode.Companion
            .ofRoundedSquares()
            .withColor(0xFF000000.toInt())
            .withSize(25)
            .build(qrData)

        val pngBytes = qrCode
            .render()

        return pngBytes.getBytes().decodeToImageBitmap()

    }
}