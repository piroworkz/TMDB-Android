package com.davidluna.tmdb.core_data.framework.remote.interceptors

import com.davidluna.tmdb.core_data.di.NativeModule
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot.Keys
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TmdbInterceptor(
    private val apiKey: NativeModule.ApiKey,
    private val parametersSnapshot: ParametersSnapshot,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            val url = buildUrl(request, parametersSnapshot())
            val newRequest = buildRequest(request, url)
            chain.proceed(newRequest)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun buildRequest(request: Request, url: HttpUrl) = request.newBuilder().apply {
        addHeader(Keys.AUTHENTICATION, "Bearer ${apiKey.value}")
        url(url)
    }.build()

    private fun buildUrl(request: Request, queryParameters: Map<String, String>) =
        request.url.newBuilder().apply {
            addQueryParameter(Keys.API_KEY, apiKey.value)

            val isImagesRequest = request.url.encodedPath.contains("images")

            queryParameters.forEach { (key, value) ->
                when (key) {
                    Keys.LANGUAGE -> if (!isImagesRequest) {
                        addQueryParameter(key, value)
                    }

                    Keys.INCLUDE_IMAGE_LANGUAGE -> if (isImagesRequest) {
                        addQueryParameter(key, value)
                    }

                    else -> addQueryParameter(key, value)
                }
            }
        }.build()
}