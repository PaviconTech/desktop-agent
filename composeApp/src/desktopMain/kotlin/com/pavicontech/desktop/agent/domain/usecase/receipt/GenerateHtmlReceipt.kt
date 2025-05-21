package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.ExtractedInvoiceData
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GenerateHtmlReceipt(
    private val generateQrCode: GenerateQrCodeUseCase
) {
    private val currencyFormatter: (Double) -> String = { "%,.2f".format(it) }
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
    val randomData = (1000..100000).random()


    operator fun invoke(
        data: ExtractedInvoiceData,
        businessInfo: BusinessInformation
    ): String {
        val userHome = System.getProperty("user.home")
        val qrPath = Paths.get(userHome, "Documents", "Receipts", "qr-code.png")

        val qrImagePath = generateQrCode.invoke(
            randomData = randomData.toString(),
            path = qrPath,
            width = 10,
            height = 10
        ).toURI().toString()
        val logoUrl = escapeHtml(businessInfo.businessLogo ?: "")
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8" />
            <title>${businessInfo.name} - Official Receipt</title>
            <style>
                body {
                    padding: 2.5rem;
                    font-family: 'Inter', system-ui, sans-serif;
                    font-size: 0.875rem;
                    color: #1A202C;
                    background: white;
                    line-height: 1.6;
                }

                .container {
                    max-width: 800px;
                    margin: 0 auto;
                }

                .header {
                    border-bottom: 2px solid #E2E8F0;
                    padding-bottom: 1rem;
                    margin-bottom: 2rem;
                }

                .logo-container {
                    max-width: 120px;
                    margin-bottom: 1rem;
                }

                .logo {
                    width: 100%;
                    object-fit: contain;
                }

                .business-info h1 {
                    font-size: 1.5rem;
                    color: #2A4365;
                    margin-bottom: 0.25rem;
                }

                .info-line {
                    font-size: 0.875rem;
                    margin-bottom: 0.25rem;
                }

                .receipt-meta {
                    margin-bottom: 1.5rem;
                    font-size: 0.875rem;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 1.5rem;
                }

                th, td {
                    padding: 0.75rem;
                    text-align: left;
                    border-bottom: 1px solid #E2E8F0;
                }

                th {
                    background-color: #F7FAFC;
                    color: #2A4365;
                    font-weight: 600;
                }

                .totals {
                    background: #F7FAFC;
                    padding: 1rem;
                    border-radius: 6px;
                }

                .total-row {
                    display: flex;
                    justify-content: space-between;
                    margin: 0.5rem 0;
                    font-size: 0.875rem;
                }

                .footer-row {
                    display: flex;
                    flex-direction: row;
                    justify-content: flex-start;
                    align-items: flex-start;
                    gap: 2rem;
                    margin-top: 2rem;
                    border-top: 2px solid #E2E8F0;
                    padding-top: 1.5rem;
                }

             

                .qr-code {
                    width: 150px;
                    height: 150px;
                    border: 1px solid #ccc;
                    padding: 5px;
                }
                .qr-meta-box div {
                    margin-bottom: 0.4rem;
                }

                @media print {
                    body {
                        padding: 1rem;
                        font-size: 12px;
                    }
                }
            </style>
        </head>
        <body>
            <div class="container">
                <table style="width: 100%; border-bottom: 2px solid #E2E8F0; margin-bottom: 1.5rem; padding-bottom: 1rem;">
                    <tr>
                            <td style="padding-left: 20px; vertical-align: top; font-size: 0.875rem; color: #1A202C;">
                            <div style="font-size: 1.25rem; font-weight: bold; color: #2A4365; margin-bottom: 8px;">
                                ${businessInfo.name}
                            </div>
                            <div>Branch: ${businessInfo.branchName}</div>
                            <div>Taxpayer: ${businessInfo.taxpayerName}</div>
                            <div>KRA PIN: ${businessInfo.kraPin}</div>
                            <div>Location: ${businessInfo.districtName}, ${businessInfo.provinceName}</div>
                            <div>SDC ID: ${businessInfo.sdcId}</div>
                            <div>Branch ID: ${businessInfo.branchId}</div>
                            <div>Sector: ${businessInfo.sectorName}</div>
                        </td>
                        ${businessInfo.businessLogo?.let {
                            """
                            <td style="width: 150px; vertical-align: top;">
                                <img src="$logoUrl" alt="${businessInfo.name} Logo" width="120" style="margin-bottom: 10px;" />
                            </td>
                            """
                        } ?: ""}

                    </tr>
                </table>


                <div class="receipt-meta">
                    <strong>Receipt Date:</strong> ${LocalDateTime.now().format(dateFormatter)}
                </div>

                <table>
                    <thead>
                        <tr>
                            <th>Item Description</th>
                            <th style="width: 80px">Qty</th>
                            <th style="width: 120px">Amount (KES)</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.items.joinToString("") {
            """
                        <tr>
                            <td>${it.itemDescription}</td>
                            <td>${it.quantity}</td>
                            <td>${currencyFormatter(it.amount)}</td>
                        </tr>
                        """
        }}
                    </tbody>
                </table>

                <div class="totals">
                    <div class="total-row">
                        <span>Subtotal:</span>
                        <span>KES ${currencyFormatter(data.subTotal)}</span>
                    </div>
                    <div class="total-row">
                        <span>Total VAT:</span>
                        <span>KES ${currencyFormatter(data.totalVat ?: 0.0)}</span>
                    </div>
                    <div class="total-row">
                        <strong>Total Amount:</strong>
                        <strong>KES ${currencyFormatter(data.totalAmount)}</strong>
                    </div>
                </div>

                <table style="width: 100%; margin-top: 2rem; border-top: 2px solid #E2E8F0; padding-top: 1.5rem;">
                    <tr>
                        <td style="width: 160px; text-align: center; vertical-align: top;">
                            <img src="$qrImagePath" alt="QR Code" width="150" height="150" style="border: 1px solid #ccc; padding: 5px;" />
                            <div style="font-size: 10px; color: #4A5568; margin-top: 5px;">Scan QR for verification</div>
                        </td>
                        <td style="padding-left: 20px; vertical-align: top; font-size: 12px; color: #4A5568;">
                            <div><strong>Internal Reference:</strong> Test1234567</div>
                            <div><strong>Receipt Signature:</strong> Test2345678</div>
                            <div><strong>VSDC Date:</strong> 19 May 2025 16:03:09</div>
                            <div><strong>MRC Number:</strong> TExt1234</div>
                            <div style="margin-top: 10px;">
                                This is a computer-generated receipt.<br />
                                No signature required • Demo TaxPoint
                            </div>
                        </td>
                    </tr>
                </table>

            </div>
        </body>
        </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }


}
