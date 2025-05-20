package com.pavicontech.desktop.agent.data.remote.dto.response.getSales


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetSalesRes(
    @SerialName("message")
    val message: String,
    @SerialName("sales")
    val sales: List<Sale>,
    @SerialName("status")
    val status: Boolean
){
/*    fun toSale() = sales.map {
*//*        com.pavicontech.desktop.agent.domain.model.Sale(
            id = it.id,
            customerName = it.custNm,
            kraPin = it.custTin,
            referenceNumber = it.
        )*//*
    }*/
}