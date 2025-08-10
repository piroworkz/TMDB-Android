package com.davidluna.tmdb.core_framework.data.remote.interceptors

import com.davidluna.tmdb.core_framework.di.qualifiers.ApiKey
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbInterceptor @Inject constructor(
    @param:ApiKey private val apiKey: String,
    private val parametersSnapshot: ParametersSnapshot
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            val url = buildUrl(request, parametersSnapshot())
            val newRequest = buildRequest(request, url)
            chain.proceed(newRequest)
        } catch (_: Exception) {
            chain.proceed(request)
        }
    }

    private fun buildRequest(request: Request, url: HttpUrl) = request.newBuilder().apply {
        addHeader("Authorization", "Bearer $apiKey")
        url(url)
    }.build()

    private fun buildUrl(request: Request, queryParameters: Map<String, String>) =
        request.url.newBuilder().apply {
            addQueryParameter("api_key", apiKey)
            queryParameters.forEach { (key, value) -> addQueryParameter(key, value) }
        }.build()
}