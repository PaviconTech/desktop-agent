package com.pavicontech.desktop.agent.data.remote

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager
import kotlin.time.Duration.Companion.minutes


object KtorClient {

    fun create(storage: KeyValueStorage): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(Auth) {
                bearer {
                  /*  loadTokens {
                        val token = storage.get(Constants.AUTH_TOKEN) ?: return@loadTokens null
                        BearerTokens(token, "")
                    }
                    sendWithoutRequest { true } */// <--- Add this line
                }
            }

            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
                //sanitizeHeader { it == HttpHeaders.Authorization }
            }

            engine {
                requestTimeout = 3.minutes.inWholeMilliseconds
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}
