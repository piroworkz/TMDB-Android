package com.davidluna.tmdb.convention.bundles

import com.davidluna.tmdb.convention.helpers.androidRunner
import com.davidluna.tmdb.convention.helpers.androidTestCore
import com.davidluna.tmdb.convention.helpers.androidTestImplementation
import com.davidluna.tmdb.convention.helpers.androidTestRules
import com.davidluna.tmdb.convention.helpers.composeBom
import com.davidluna.tmdb.convention.helpers.coroutinesTest
import com.davidluna.tmdb.convention.helpers.debugImplementation
import com.davidluna.tmdb.convention.helpers.hiltCompiler
import com.davidluna.tmdb.convention.helpers.hiltTest
import com.davidluna.tmdb.convention.helpers.kspAndroidTest
import com.davidluna.tmdb.convention.helpers.libs
import com.davidluna.tmdb.convention.helpers.mockWebServer
import com.davidluna.tmdb.convention.helpers.navigationTesting
import com.davidluna.tmdb.convention.helpers.playServicesLocation
import com.davidluna.tmdb.convention.helpers.uiTestJunit4
import com.davidluna.tmdb.convention.helpers.uiTestManifest
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal val Project.androidTestingBundle: Unit
    get() {
        dependencies {
            androidHiltTestingBundle
            androidTestImplementation(platform(libs.composeBom))
            androidTestImplementation(libs.uiTestJunit4)
            androidTestImplementation(libs.navigationTesting)
            debugImplementation(libs.uiTestManifest)
            androidTestImplementation(libs.androidRunner)
            androidTestImplementation(libs.androidTestRules)
            androidTestImplementation(libs.androidTestCore)
            androidTestImplementation(libs.mockWebServer)
            androidTestImplementation(libs.coroutinesTest)
            androidTestImplementation(libs.playServicesLocation)
        }
    }

internal val Project.androidHiltTestingBundle: Unit
    get() = dependencies {
        androidTestImplementation(libs.coroutinesTest)
        androidTestImplementation(libs.hiltTest)
        kspAndroidTest(libs.hiltCompiler)
    }