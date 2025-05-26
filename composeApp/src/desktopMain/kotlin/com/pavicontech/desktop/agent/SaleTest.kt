package com.pavicontech.desktop.agent

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.KtorClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject


val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjYyLCJidXNpbmVzc0lkIjo0NywidXNlcm5hbWUiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ4MDg1NTY0LCJleHAiOjE3NDgzODU1NjR9.REhnDbdecGc7174wCIKNk4Epqu1xWCAkOxQvZROhKXA"
fun body():String{
    val str = """
        {
            "invcNo": "1",
            "orgInvcNo": 0,
            "salesTyCd": "N",
            "rcptTyCd": "S",
            "salesSttsCd": "02",
            "cfmDt": "20250524135213",
            "salesDt": "20250524",
            "stockRlsDt": "20250524135213",
            "cnclReqDt": null,
            "cnclDt": null,
            "rfdDt": null,
            "pmtTyCd": "01",
            "rfdRsnCd": null,
            "totItemCnt": 1,
            "taxRtA": 0,
            "taxRtB": 16,
            "taxRtC": 0,
            "taxRtD": 0,
            "taxRtE": 8,
            "taxAmtA": 0,
            "taxAmtB": 0,
            "taxAmtC": 0,
            "taxAmtD": 0,
            "taxAmtE": 0,
            "totTaxblAmt": 100,
            "totTaxAmt": 0,
            "totAmt": 100,
            "prchrAcptcYn": "N",
            "remark": "",
            "regrId": "Admin",
            "regrNm": "Admin",
            "modrId": "Admin",
            "modrNm": "Admin",
            "receipt": {
                "rptNo": 1,
                "trdeNm": "",
                "topMsg": "Sale of VSCU",
                "btmMsg": "Bottom Sale of VSCU",
                "prchrAcptcYn": "N"
            },
            "itemList": [
                {
                    "itemSeq": 1,
                    "itemId": 454,
                    "itemCd": "KE2OUU0",
                    "itemClsCd": "85121600",
                    "itemNm": "ULTRASOUND-PELVIC",
                    "itemCodeDf": "KE2OUU0",
                    "bcd": "09180910191010",
                    "pkgUnitCd": "OU",
                    "qty": 1,
                    "qtyUnitCd": "U",
                    "prc": 200,
                    "splyAmt": "100.00",
                    "dcRt": "0.00",
                    "dcAmt": 0,
                    "isrccCd": null,
                    "isrccNm": null,
                    "isrcRt": null,
                    "isrcAmt": null,
                    "taxTyCd": "D",
                    "taxblAmt": "100.00",
                    "taxAmt": "0.00",
                    "totAmt": 100,
                    "description": "",
                    "id": 1
                }
            ]
        }
    """.trimIndent()

    return str
}
fun main():Unit = runBlocking{
    KtorClient.create().post("https://etims.pavicontech.com/api/sales"){
        header(HttpHeaders.Authorization, "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(body())
    }.bodyAsText().let { it.logger(Type.DEBUG) }
}