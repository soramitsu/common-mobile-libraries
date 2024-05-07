package jp.co.soramitsu.xnetworking.core.engines.rest.impl

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.userAgent
import jp.co.soramitsu.xnetworking.core.engines.rest.api.RestClient
import jp.co.soramitsu.xnetworking.core.engines.rest.api.models.AbstractRestClientConfig
import jp.co.soramitsu.xnetworking.core.engines.rest.api.models.AbstractRestServerRequest
import jp.co.soramitsu.xnetworking.core.engines.rest.api.models.RestClientException
import jp.co.soramitsu.xnetworking.core.engines.rest.impl.builder.impl.HttpClientBuilderImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException

class RestClientImpl(
    private val restClientConfig: AbstractRestClientConfig
): RestClient {

    private val httpClient = HttpClientBuilderImpl()
        .build(restClientConfig)

    override val config: AbstractRestClientConfig
        get() = restClientConfig

    override suspend fun <T> post(
        request: AbstractRestServerRequest.WithBody,
        kSerializer: KSerializer<T>
    ): T = config.getOrCreateJsonConfig().decodeFromString(
        deserializer = kSerializer,
        string = wrapInExceptionHandler {
            httpClient.post(request.url) {
                if (request.requestContentType === RestClient.ContentType.JSON)
                    contentType(ContentType.Application.Json)

                if (request.responseContentType === RestClient.ContentType.JSON)
                    accept(ContentType.Application.Json)

                request.headers?.filterNot { (key, value) ->
                    key.isBlank() || value.isBlank()
                }?.forEach { (key, value) -> header(key, value) }

                request.bearerToken.apply {
                    if (!this.isNullOrBlank())
                        bearerAuth(this)
                }

                request.userAgent.apply {
                    if (!this.isNullOrBlank())
                        userAgent(this)
                }

                setBody(request.body)
            }.bodyAsText()
        }
    )

    override suspend fun <T> get(
        request: AbstractRestServerRequest,
        kSerializer: KSerializer<T>
    ): T = config.getOrCreateJsonConfig().decodeFromString(
        deserializer = kSerializer,
        string = wrapInExceptionHandler {
            httpClient.get(request.url) {
                request.bearerToken.apply {
                    if (!this.isNullOrBlank())
                        bearerAuth(this)
                }

                request.headers?.filterNot { (key, value) ->
                    key.isBlank() || value.isBlank()
                }?.forEach { (key, value) -> header(key, value) }

                request.userAgent.apply {
                    if (!this.isNullOrBlank())
                        userAgent(this)
                }

                if (request.responseContentType === RestClient.ContentType.JSON)
                    accept(ContentType.Application.Json)

                request.queryParams?.forEach { (queryName, queryValue) ->
                    parameter(queryName, queryValue)
                }
            }.bodyAsText()
        }
    )

    private suspend fun <Type> wrapInExceptionHandler(
        block: suspend () -> Type
    ): Type = try {
        block.invoke()
    } catch (e: ResponseException) {
        val code: Int = when (e) {
            is RedirectResponseException -> 3
            is ClientRequestException -> 4
            is ServerResponseException -> 5
            else -> 0
        }
        throw RestClientException.WithCode(code, e.message.orEmpty(), e.cause)
    } catch (e: SerializationException) {
        throw RestClientException.WhileSerialization(e.message.orEmpty(), e.cause)
    } catch (e: Throwable) {
        throw RestClientException.SimpleException(e.message.orEmpty(), e.cause)
    }

}