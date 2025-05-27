package com.pavicontech.desktop.agent.data.local.database.entries


import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.time.Instant

@Serializable
data class Invoice(
    val id: String = ObjectId().toString(),
    val fileName: String,
    val extractionStatus: ExtractionStatus = ExtractionStatus.PENDING,
    val etimsStatus: EtimsStatus = EtimsStatus.PENDING,
    val items: List<Item> = emptyList(),
    val totals: Totals? =null,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String? = null
)

@Serializable
data class Item(
    val amount: Double,
    val itemDescription: String,
    val quantity: Double,
    val taxAmount: Double,
    val taxPercentage: Double,
    val taxType: String? = null
)


@Serializable
data class Totals(
    val subTotal: Double,
    val totalAmount: Double,
    val totalVat: Double
)


enum class ExtractionStatus{
    PENDING,
    SUCCESSFUL,
    FAILED,
}

enum class EtimsStatus{
    PENDING,
    SUCCESSFUL,
    FAILED,
}


