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


import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


@Serializable(with = CreateSaleResSerializer::class)
@JsonIgnoreUnknownKeys
data class CreateSaleRes(
    @SerialName("ResultCd") val status: String = "",
    @SerialName("ResultDt") val resultDt: String = "",
    @SerialName("ResultMsg") val resultMsg: String? = null,
    @SerialName("Data") val kraResult: KraResult? = null,
)


object CreateSaleResSerializer : KSerializer<CreateSaleRes> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CreateSaleRes") {
        element<String>("ResultCd")
        element<String>("ResultDt")
        element<String>("ResultMsg", isOptional = true)
        element<KraResult>("Data", isOptional = true)
    }

    override fun deserialize(decoder: Decoder): CreateSaleRes {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can only deserialize with Json")
        val element = jsonDecoder.decodeJsonElement() as JsonObject

        return if ("ResultCd" in element) {
            // Standard KRA response
            val dataObj = element["Data"]?.jsonObject
            val kraResult = dataObj?.let {
                KraResult(
                    totRcptNo = it["TotRcptNo"]?.jsonPrimitive?.int ?: 0,
                    rcptNo = it["RcptNo"]?.jsonPrimitive?.int ?: 0,
                    intrlData = it["IntrlData"]?.jsonPrimitive?.contentOrNull,
                    rcptSign = it["RcptSign"]?.jsonPrimitive?.contentOrNull,
                    sdcDateTime = it["sdcDateTime"]?.jsonPrimitive?.contentOrNull,
                    vsdcRcptPbctDate = it["VsdcRcptPbctDate"]?.jsonPrimitive?.content ?: "",
                    invoiceNo = it["invoiceNo"]?.jsonPrimitive?.int ?: 0,
                    sdcId = it["SdcId"]?.jsonPrimitive?.content ?: "",
                    mrcNo = it["MrcNo"]?.jsonPrimitive?.contentOrNull,
                    qrUrl = it["Qrurl"]?.jsonPrimitive?.content ?: ""
                )
            }
            CreateSaleRes(
                status = element["ResultCd"]?.jsonPrimitive?.content ?: "",
                resultDt = element["ResultDt"]?.jsonPrimitive?.content ?: "",
                resultMsg = element["ResultMsg"]?.jsonPrimitive?.contentOrNull,
                kraResult = kraResult
            )
        } else if ("status" in element) {
            // Alternative response
            val statusBool = element["status"]?.jsonPrimitive?.booleanOrNull ?: false
            val message = element["message"]?.jsonPrimitive?.contentOrNull
            val resultObj = element["result"]?.jsonObject
            val kraResult = resultObj?.let {
                KraResult(
                    totRcptNo = it["totRcptNo"]?.jsonPrimitive?.int ?: 0,
                    rcptNo = it["rcptNo"]?.jsonPrimitive?.int ?: 0,
                    intrlData = it["intrlData"]?.jsonPrimitive?.contentOrNull,
                    rcptSign = it["rcptSign"]?.jsonPrimitive?.contentOrNull,
                    sdcDateTime = null,
                    vsdcRcptPbctDate = it["vsdcRcptPbctDate"]?.jsonPrimitive?.content ?: "",
                    invoiceNo = it["rcptNo"]?.jsonPrimitive?.int ?: 0,
                    sdcId = it["sdcId"]?.jsonPrimitive?.content ?: "",
                    mrcNo = it["mrcNo"]?.jsonPrimitive?.contentOrNull,
                    qrUrl = it["rcptSign"]?.jsonPrimitive?.contentOrNull ?: ""
                )
            }
            CreateSaleRes(
                status = if (statusBool) "000" else "999",
                resultDt = "",
                resultMsg = message,
                kraResult = kraResult
            )
        } else {
            // Unknown format
            CreateSaleRes()
        }
    }

    override fun serialize(encoder: Encoder, value: CreateSaleRes) {
        val obj = buildJsonObject {
            put("ResultCd", value.status)
            put("ResultDt", value.resultDt)
            put("ResultMsg", value.resultMsg)
            value.kraResult?.let { kr ->
                put("Data", buildJsonObject {
                    put("TotRcptNo", kr.totRcptNo)
                    put("RcptNo", kr.rcptNo)
                    put("IntrlData", kr.intrlData)
                    put("RcptSign", kr.rcptSign)
                    put("sdcDateTime", kr.sdcDateTime)
                    put("VsdcRcptPbctDate", kr.vsdcRcptPbctDate)
                    put("invoiceNo", kr.invoiceNo)
                    put("SdcId", kr.sdcId)
                    put("MrcNo", kr.mrcNo)
                    put("Qrurl", kr.qrUrl)
                })
            }
        }

        encoder.encodeSerializableValue(JsonObject.serializer(), obj)
    }
}
