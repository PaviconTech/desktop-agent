package com.pavicontech.desktop.agent.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SavedUserCredentials(
    val kraPin:String,
    val username:String
){
    fun toJson() = Json.encodeToString(this)
}
