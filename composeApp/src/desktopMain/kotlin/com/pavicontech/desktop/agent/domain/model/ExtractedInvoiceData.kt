package com.pavicontech.desktop.agent.domain.model


data class ExtractedInvoiceData(
    val fileName: String,
    val items: List<ExtractedItem> = emptyList(),
    val subTotal: Double,
    val totalAmount: Double,
    val totalVat: Double? = null
    )


data class ExtractedItem(
    val name: String,
    val amount: Double,
    val itemDescription: String,
    val quantity: Double,
    val taxAmount: Double? = null,
    val taxPercentage: Double? = null,
    val taxType: String? = null
)

    /*
    fun ExtractedInvoiceData.toCreateSaleBody(){
        CreateSaleReq(

        )
    }


    fun Item.toCreateSaleItem(): CreateSaleItem{
        return CreateSaleItem(
            dcAmt = 0,
            dcRt = 0,
            itemCd =
        )
    }*/
