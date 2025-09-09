package com.davidluna.tmdb.convention.extensions.android_library

import com.android.build.api.dsl.LibraryExtension
import com.davidluna.tmdb.convention.constants.Constants

internal fun LibraryExtension.defaultConfig() {
    compileSdk = Constants.COMPILE_SDK
    defaultConfig {
        minSdk = Constants.MIN_SDK
        testInstrumentationRunner = Constants.TEST_INSTRUMENTATION_RUNNER
        consumerProguardFiles("proguard-rules.pro")
    }
}