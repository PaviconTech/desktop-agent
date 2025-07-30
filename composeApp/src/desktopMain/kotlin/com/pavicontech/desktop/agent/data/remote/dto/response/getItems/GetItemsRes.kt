package com.pavicontech.desktop.agent.data.remote.dto.response.getItems


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GetItemsRes(
    @SerialName("items")
    val items: List<Item>,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean
){
    fun toItemListString() = Json.encodeToString(items)

    companion object{
        fun fromJson(json: String) = Json.decodeFromString<List<Item>>(json)
    }
}