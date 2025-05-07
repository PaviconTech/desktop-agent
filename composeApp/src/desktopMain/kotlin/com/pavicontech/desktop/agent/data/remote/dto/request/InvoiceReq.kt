package com.pavicontech.desktop.agent.data.remote.dto.request

data class InvoiceReq(
    val id: Int,
    val name: String,
    val branchId: String,
    val branchName: String,
    val districtName: String,
    val kraPin: String,
    val provinceName: String,
    val sectorName: String,
    val sdcId: String,
    val taxpayerName: String,
    val fileName: String = "",
    val file: ByteArray? = null
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
