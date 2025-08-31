package com.davidluna.tmdb.convention.constants

import org.gradle.api.JavaVersion

object Constants {
    const val NAMESPACE = "com.davidluna.tmdb"
    const val COMPILE_SDK = 35
    const val MIN_SDK = 28
    const val TARGET_SDK = 35
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0"
    const val HILT_TEST_RUNNER = "com.davidluna.tmdb.app.HiltTestRunner"
    const val API_KEY = "MY_API_KEY"
    const val BASE_URL = "BASE_URL"
    const val KEY_ALIAS = "MY_KEY_ALIAS"
    const val KEY_PASSWORD = "MY_KEY_PASSWORD"
    const val STORE_FILE = "MY_STORE_FILE"
    const val STORE_PASSWORD = "MY_STORE_PASSWORD"

    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_17

    val ABI_FILTERS = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

}