package com.pavicontech.desktop.agent.data.remote

import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
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
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


object KtorClient {

    fun create(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        coerceInputValues = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        this.prettyPrint = true
                        this.explicitNulls = true
                    }
                )
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.HEADERS
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                retryIf(maxRetries = 3) { request, response ->
                    response.status.value in 500..599 // Handles 500, 502, 503, 504 etc.
                }
                retryOnException(maxRetries = 3, retryOnTimeout = true)
                delayMillis { retry -> // exponential backoff: 500ms, 1000ms, 1500ms...
                    retry * 500L
                }
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
