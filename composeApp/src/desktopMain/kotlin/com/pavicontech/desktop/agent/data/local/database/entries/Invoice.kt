package com.pavicontech.desktop.agent.data.local.database.entries


import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val extractionStatus: ExtractionStatus = ExtractionStatus.PENDING,
    val etimsStatus: EtimsStatus = EtimsStatus.PENDING,
    val items: List<Item> = emptyList(),
    val totals: Totals? =null,
    val createdAt: String = Instant.now().toString(),
    val updatedAt: String? = Instant.now().toString()
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


