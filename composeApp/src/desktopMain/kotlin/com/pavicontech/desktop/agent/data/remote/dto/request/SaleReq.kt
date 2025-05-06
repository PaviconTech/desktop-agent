package com.pavicontech.desktop.agent.data.remote.dto.request

class SaleReq(
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
    val fileName:String,
    val file:ByteArray
) {
}