package com.davidluna.tmdb.convention.extensions.application


import com.android.build.api.dsl.ApplicationExtension
import com.davidluna.tmdb.convention.constants.*
import com.davidluna.tmdb.convention.constants.Constants.COMPILE_SDK
import com.davidluna.tmdb.convention.constants.Constants.HILT_TEST_RUNNER
import com.davidluna.tmdb.convention.constants.Constants.MIN_SDK
import com.davidluna.tmdb.convention.constants.Constants.NAMESPACE
import com.davidluna.tmdb.convention.constants.Constants.TARGET_SDK
import com.davidluna.tmdb.convention.constants.Constants.VERSION_CODE
import com.davidluna.tmdb.convention.constants.Constants.VERSION_NAME
import org.gradle.api.Project
import java.io.File

internal fun ApplicationExtension.setDefaultConfig(project: Project) {
    namespace = NAMESPACE
    compileSdk = COMPILE_SDK

    defaultConfig {
        applicationId = NAMESPACE
        minSdk = MIN_SDK
        targetSdk = TARGET_SDK
        versionCode = VERSION_CODE
        versionName = VERSION_NAME
        testInstrumentationRunner = HILT_TEST_RUNNER
        vectorDrawables {
            useSupportLibrary = true
        }

        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            ndkBuild {
                arguments(
                    "${Constants.API_KEY}=${project.property(Constants.API_KEY)}",
                    "${Constants.BASE_URL}=${project.property(Constants.BASE_URL)}",
                    "APP_LDFLAGS+=-Wl,-z,max-page-size=16384"
                )

            }
        }

        ndk {
            abiFilters.addAll(Constants.ABI_FILTERS)
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = File("src/main/jni/Android.mk")
        }
    }
}