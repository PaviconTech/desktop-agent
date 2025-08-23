package com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes

import com.pavicontech.desktop.agent.common.Constants
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonIgnoreUnknownKeys


import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
@Serializable(with = CreateSaleResSerializer::class)
@JsonIgnoreUnknownKeys
data class CreateSaleRes(
    @SerialName("ResultCd") val status: String = "",
    @SerialName("ResultDt") val resultDt: String = "",
    @SerialName("ResultMsg") val resultMsg: String? = null,
    @SerialName("Data") val kraResult: KraResult? = null,
)


@OptIn(ExperimentalSerializationApi::class)
object CreateSaleResSerializer : KSerializer<CreateSaleRes> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CreateSaleRes") {
        element<String>("ResultCd", isOptional = true)
        element<String>("ResultDt", isOptional = true)
        element<String>("ResultMsg", isOptional = true)
        element<KraResult>("Data", isOptional = true)
    }

    override fun deserialize(decoder: Decoder): CreateSaleRes {
        val input = decoder as? JsonDecoder ?: error("Can only deserialize with Json")
        val element = input.decodeJsonElement() as JsonObject

        return if ("ResultCd" in element) {
            // Standard KRA response → decode directly
            Json.decodeFromJsonElement(CreateSaleRes.serializer(), element)
        } else {
            // Alt response with "status", "message", "result"
            val status = element["status"]?.jsonPrimitive?.booleanOrNull ?: false
            val message = element["message"]?.jsonPrimitive?.contentOrNull
            val result = element["result"]?.jsonObject

            val kraResult = result?.let {
                KraResult(
                    totRcptNo = it["totRcptNo"]?.jsonPrimitive?.int ?: 0,
                    rcptNo = it["rcptNo"]?.jsonPrimitive?.int ?: 0,
                    intrlData = it["intrlData"]?.jsonPrimitive?.contentOrNull,
                    rcptSign = it["rcptSign"]?.jsonPrimitive?.contentOrNull,
                    sdcDateTime = null,
                    vsdcRcptPbctDate = it["vsdcRcptPbctDate"]?.jsonPrimitive?.content ?: "",
                    invoiceNo = it["rcptNo"]?.jsonPrimitive?.int ?: 0, // fallback
                    sdcId = it["sdcId"]?.jsonPrimitive?.content ?: "",
                    mrcNo = it["mrcNo"]?.jsonPrimitive?.contentOrNull,
                    qrUrl = "${Constants.ETIMS_QR_URL}${Constants.bussinesPin}${Constants.branchId}${it["rcptSign"]?.jsonPrimitive?.contentOrNull}"
                )
            }

            CreateSaleRes(
                status = if (status) "000" else "999",
                resultDt = "",
                resultMsg = message,
                kraResult = kraResult
            )
        }
    }

    override fun serialize(encoder: Encoder, value: CreateSaleRes) {
        Json.encodeToJsonElement(CreateSaleRes.serializer(), value).let { element ->
            encoder.encodeSerializableValue(JsonObject.serializer(), element.jsonObject)
        }
    }
}
