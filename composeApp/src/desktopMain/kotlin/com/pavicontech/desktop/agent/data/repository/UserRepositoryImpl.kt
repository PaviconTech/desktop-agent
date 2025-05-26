package com.pavicontech.desktop.agent.data.repository

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.remote.dto.request.SignInReq
import com.pavicontech.desktop.agent.data.remote.dto.response.signIn.SignInRes
import com.pavicontech.desktop.agent.domain.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class UserRepositoryImpl(private val api:HttpClient):UserRepository {
    override suspend fun signIn(body: SignInReq): SignInRes = withContext(Dispatchers.IO){
        api.post(urlString = "${Constants.ETIMS_BACKEND}/auth/login"){
            contentType(ContentType.Application.Json)
            setBody(body)
        }.bodyAsText().let {
            it.logger(Type.INFO)
            Json.decodeFromString<SignInRes>(it)
        }
    }
}