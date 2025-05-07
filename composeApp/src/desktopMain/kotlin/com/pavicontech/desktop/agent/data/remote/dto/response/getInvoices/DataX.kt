package com.pavicontech.desktop.agent.data.remote.dto.response.getInvoices


import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject

@Serializable
data class DataX(
    @SerialName("branchId")
    val branchId: String,
    @SerialName("branchName")
    val branchName: String,
    @SerialName("districtName")
    val districtName: String,
    @SerialName("document_id")
    val documentId: String,
    @SerialName("fees")
    val fees: List<Fee> = emptyList(),
    @SerialName("fileName")
    val fileName: String,
    @SerialName("file_url")
    val fileUrl: String,
    @SerialName("items")
    val items: List<Item>,
    @SerialName("kraPin")
    val kraPin: String,
    @SerialName("name")
    val name: String,
    @SerialName("provinceName")
    val provinceName: String,
    @SerialName("sdcId")
    val sdcId: String,
    @SerialName("sectorName")
    val sectorName: String,
    @SerialName("taxpayerName")
    val taxpayerName: String,
    @SerialName("totals")
    val totals: Totals
)

object DataXSerializer : KSerializer<DataX> {
    override val descriptor: SerialDescriptor = DataX.serializer().descriptor

    override fun deserialize(decoder: Decoder): DataX {
        val input = decoder as? JsonDecoder
            ?: throw SerializationException("Expected JsonDecoder") as Throwable

        val element = input.decodeJsonElement()
        return if (element.jsonObject.isEmpty()) {
            DataX(
                branchId = "",
                branchName = "",
                districtName = "",
                documentId = "",
                fees = emptyList(),
                fileName = "",
                fileUrl = "",
                items = emptyList(),
                kraPin = "",
                name = "",
                provinceName = "",
                sdcId = "",
                sectorName = "",
                taxpayerName = "",
                totals = Totals()
            ) // return default
        } else {
            input.json.decodeFromJsonElement(DataX.serializer(), element)
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: DataX
    ) {
        DataX.serializer().serialize(encoder, value)
    }
}
