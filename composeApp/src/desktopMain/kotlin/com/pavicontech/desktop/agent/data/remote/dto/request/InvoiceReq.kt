package com.pavicontech.desktop.agent.data.remote.dto.request

data class InvoiceReq(
    val fileName: String = "",
    val file: ByteArray? = null,
    val invoiceWords: String? = null,
    val bussinessPin: String,
    val items: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InvoiceReq

        if (!file.contentEquals(other.file)) return false

        return true
    }

    override fun hashCode(): Int {
        return file.contentHashCode()
    }
}
