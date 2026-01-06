package com.davidluna.tmdb.core_data.framework.remote.interceptors

interface ParametersSnapshot {
    operator fun invoke(): Map<String, String>

    companion object Keys {
        const val REGION = "region"
        const val LANGUAGE = "language"
        const val INCLUDE_IMAGE_LANGUAGE = "include_image_language"
        const val SESSION_ID = "session_id"
        const val API_KEY = "api_key"
        const val DEFAULT_LANGUAGE = "en-us"
        const val AUTHENTICATION = "Authorization"
    }
}