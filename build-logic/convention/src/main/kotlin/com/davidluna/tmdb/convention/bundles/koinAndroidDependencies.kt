package com.davidluna.tmdb.convention.bundles

import com.davidluna.tmdb.convention.helpers.arrowCore
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.koinAndroid
import com.davidluna.tmdb.convention.helpers.koinAnnotations
import com.davidluna.tmdb.convention.helpers.koinBom
import com.davidluna.tmdb.convention.helpers.koinComposeNavigation
import com.davidluna.tmdb.convention.helpers.koinKspCompiler
import com.davidluna.tmdb.convention.helpers.kotlinConverter
import com.davidluna.tmdb.convention.helpers.kotlinCoroutinesCore
import com.davidluna.tmdb.convention.helpers.kotlinxSerializationJson
import com.davidluna.tmdb.convention.helpers.ksp
import com.davidluna.tmdb.convention.helpers.libs
import com.davidluna.tmdb.convention.helpers.okhttpClient
import com.davidluna.tmdb.convention.helpers.okhttpLoggingInterceptor
import com.davidluna.tmdb.convention.helpers.retrofit
import com.davidluna.tmdb.convention.helpers.roomCompiler
import com.davidluna.tmdb.convention.helpers.roomKtx
import com.davidluna.tmdb.convention.helpers.roomRuntime
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.koinAndroidDependencies(setNav: Boolean = false) {
    dependencies {
        implementation(platform(libs.koinBom))
        implementation(libs.koinAndroid)
        implementation(libs.koinAnnotations)
        ksp(libs.koinKspCompiler)
        if (setNav) {
            implementation(libs.koinComposeNavigation)
        }
    }
}

fun Project.appHttpClientDependencies() {
    dependencies {
        implementation(libs.kotlinConverter)
        implementation(libs.okhttpClient)
        implementation(libs.okhttpLoggingInterceptor)
        implementation(libs.retrofit)
    }
}

fun Project.dataHttpClientDependencies() {
    dependencies {
        implementation(libs.kotlinxSerializationJson)
        implementation(libs.retrofit)
    }
}

fun Project.kotlinLibsDependencies() {
    dependencies {
        implementation(libs.arrowCore)
        implementation(libs.kotlinCoroutinesCore)
    }
}

fun Project.roomDependencies() {
    dependencies {
        implementation(libs.roomKtx)
        implementation(libs.roomRuntime)
        ksp(libs.roomCompiler)
    }
}
