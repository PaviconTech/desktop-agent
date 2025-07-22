package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.KraResult
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.ExtractedInvoiceData
import qrcode.QRCode
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

class GenerateHtmlReceipt80MMUseCase(
    private val generateQrCode: GenerateQrCodeUseCase
) {
    private val currencyFormatter: (Double) -> String = { "%,.2f".format(it) }
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")

    operator fun invoke(
        data: ExtractedInvoiceData,
        businessPin: String,
        bhfId: String,
        rcptSign: String,
        businessInfo: BusinessInformation,
        kraResult: KraResult?
    ): String {
        val userHome = System.getProperty("user.home")
        val qrPath = Paths.get(userHome, "Documents", "Receipts", "qr-code.png")
        "Printing data: ${data.toString()}".logger(Type.INFO)
        "Printing Business info: $businessInfo".logger(Type.INFO)

        // Reduce QR size
        val qrImagePath = generateQrCode(
            path = qrPath,
            businessPin = businessPin,
            bhfId = bhfId,
            rcptSign = rcptSign,
        ).toURI().toString()


        val businessHeaderHtml = buildString {
            appendLine("""<div class="center">""")
            appendLine("""<b>${businessInfo.name}</b><br/>""")
            businessInfo.address?.takeIf { it.isNotBlank() }?.let {
                appendLine("""<b>$it</b><br/>""")
            }



            businessInfo.email?.takeIf { it.isNotBlank() }?.let {
                appendLine("""<b>Email: $it</b><br/>""")
            }

            businessInfo.phone?.takeIf { it.isNotBlank() }?.let {
                appendLine("""<b>Phone: $it</b><br/>""")
            }

            appendLine("""<b>PIN: ${businessInfo.kraPin}</b><br/>""")
            appendLine("""<b>Branch: ${businessInfo.branchName}</b><br/>""")

            appendLine("</div>")
        }



        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <style>
        body {
            font-family: Arial, sans-serif;
            width: 400px;
            font-size: 18px;
            margin: 0 auto;
            padding: 0;
            line-height: 1.3;
        }

    .center {
        text-align: center;
        margin: 0 auto;
        width: 100%;
    }

    .bold {
        font-weight: bold;
        font-size: 16px;
        line-height: 1.4;
    }
        
        

        .divider {
            border-top: 1px dashed #000;
            margin: 6px 0;
        }

        .header, .footer { margin: 5px 0; }

        .receipt-meta {
            margin: 2px 0;
            font-size: 16px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 5px;
        }

        th, td {
            padding: 3px 0;
            text-align: left;
        }

        th {
            font-weight: bold;
            border-bottom: 1px solid #000;
            font-size: 20px;
        }

        td {
            font-size: 14px;
        }

        .right { text-align: right; }
        .total-row { font-weight: bold; margin-top: 5px; }

        .qr-row {
            display: flex;
            align-items: flex-start;
            gap: 6px;
            margin-top: 6px;
        }

        .qr {
            width: 60px;
            height: 60px;
        }

        .qr-details {
            font-size: 14px;
            line-height: 1.3;
            flex: 1;
        }

        .footer-note {
            font-size: 10px;
            margin-top: 6px;
        }
    </style>
</head>
<body>

    <!-- Header with full business details -->
    $businessHeaderHtml

    <div class="divider"></div>

    <!-- Receipt metadata and customer info -->
    <div class="receipt-meta">Date: ${LocalDateTime.now().format(dateFormatter)}</div>
    <div class="receipt-meta">Branch ID: ${businessInfo.branchId}</div>
    
    ${data.customerName?.let { "<div class='receipt-meta'>Customer: $it</div>" } ?: ""}
    ${data.customerPin?.let { "<div class='receipt-meta'>Customer PIN: $it</div>" } ?: ""}

    <div class="divider"></div>

    <!-- Items -->
    <table>
        <thead>
            <tr>
                <th>Item</th>
                <th>Qty</th>
                <th style="text-align:right;">Amount</th>
            </tr>
        </thead>
        <tbody>
            ${
            data.items.joinToString("") {
                "<tr><td>${it.itemDescription}</td><td>${it.quantity}</td><td style='text-align:right;'>${
                    currencyFormatter(
                        it.amount
                    )
                }</td></tr>"
            }
        }
        </tbody>
    </table>

    <div class="divider"></div>

    <!-- Totals -->
    <div>Subtotal: KES ${currencyFormatter(data.subTotal)}</div>
    <div>VAT: KES ${currencyFormatter(data.totalVat ?: 0.0)}</div>
    <div class="total-row">Total: KES ${currencyFormatter(data.totalAmount)}</div>

    <div class="divider"></div>

    <!-- QR and Footer -->
    <div class="qr-row">
        <img class="qr" src="$qrImagePath" alt="QR Code"/>
        <div class="qr-details">
            INTRLDATA: ${kraResult?.intrlData}<br/>
            RCPTSIGN: ${kraResult?.rcptSign}<br/>
            VSDC DATE: ${kraResult?.vsdcRcptPbctDate}<br/>
            MRCNO: ${kraResult?.mrcNo}
        </div>
    </div>

    <div class="center" style="font-size: 11px; margin-top: 5px;">
        Scan QR for verification
    </div>

    <div class="divider"></div>

    <div class="center bold">Thank you for your business!</div>

</body>
</html>
""".trimIndent()

    }


    private fun generateQrCode(
        path: Path,
        businessPin: String,
        bhfId: String,
        rcptSign: String
    ): File {
        val qrCode = QRCode
            .ofRoundedSquares()
            .withColor(0xFF000000.toInt())
            .withSize(25)
            .build("https://etims-sbx.kra.go.ke/common/link/etims/receipt/indexEtimsReceiptData?Data=$businessPin$bhfId$rcptSign")

        val pngBytes = qrCode.render().getBytes()

        val originalImage = ImageIO.read(ByteArrayInputStream(pngBytes))

        val resizedImage = originalImage.getScaledInstance(180, 180, Image.SCALE_SMOOTH)
        val outputImage = BufferedImage(180, 180, BufferedImage.TYPE_INT_ARGB)
        val graphics = outputImage.createGraphics()

        // Rendering Hints for quality
        val hints = java.awt.RenderingHints(
            java.awt.RenderingHints.KEY_INTERPOLATION,
            java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC
        )
        hints[java.awt.RenderingHints.KEY_RENDERING] = java.awt.RenderingHints.VALUE_RENDER_QUALITY
        hints[java.awt.RenderingHints.KEY_ANTIALIASING] = java.awt.RenderingHints.VALUE_ANTIALIAS_ON

        graphics.setRenderingHints(hints)
        graphics.drawImage(resizedImage, 0, 0, null)
        graphics.dispose()

        val qrFile = path.toFile()
        qrFile.parentFile.mkdirs()
        ImageIO.write(outputImage, "png", qrFile)

        return qrFile
    }



    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}
