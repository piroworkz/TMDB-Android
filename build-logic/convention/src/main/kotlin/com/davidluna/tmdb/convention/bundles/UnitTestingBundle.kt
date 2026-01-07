package com.davidluna.tmdb.convention.bundles

import com.davidluna.tmdb.convention.helpers.coroutinesTest
import com.davidluna.tmdb.convention.helpers.junit
import com.davidluna.tmdb.convention.helpers.libs
import com.davidluna.tmdb.convention.helpers.mockKAndroid
import com.davidluna.tmdb.convention.helpers.testImplementation
import com.davidluna.tmdb.convention.helpers.turbine
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal val Project.unitTestingBundle: Unit
    get() {
        dependencies {
            testImplementation(libs.junit)
            testImplementation(libs.coroutinesTest)
            testImplementation(libs.turbine)
            testImplementation(libs.mockKAndroid)
        }
    }