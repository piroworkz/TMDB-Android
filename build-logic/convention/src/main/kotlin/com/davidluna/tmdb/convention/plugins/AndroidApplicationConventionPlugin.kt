@file:Suppress("unused")

package com.davidluna.tmdb.convention.plugins

import com.davidluna.tmdb.convention.bundles.androidTestingBundle
import com.davidluna.tmdb.convention.bundles.composeUiBundle
import com.davidluna.tmdb.convention.bundles.appHttpClientDependencies
import com.davidluna.tmdb.convention.bundles.koinAndroidDependencies
import com.davidluna.tmdb.convention.bundles.unitTestingBundle
import com.davidluna.tmdb.convention.extensions.application.application
import com.davidluna.tmdb.convention.extensions.common.applicationPluginManager
import com.davidluna.tmdb.convention.helpers.arrowCore
import com.davidluna.tmdb.convention.helpers.biometric
import com.davidluna.tmdb.convention.helpers.coilCompose
import com.davidluna.tmdb.convention.helpers.composeActivity
import com.davidluna.tmdb.convention.helpers.composeNavigation
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.kotlinxSerializationJson
import com.davidluna.tmdb.convention.helpers.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        applicationPluginManager
        application
        setDependencies()
    }

    private fun Project.setDependencies() {
        dependencies {
            androidTestingBundle
            composeUiBundle
            appHttpClientDependencies()
            implementation(libs.arrowCore)
            implementation(libs.biometric)
            implementation(libs.coilCompose)
            implementation(libs.composeActivity)
            implementation(libs.composeNavigation)
            implementation(libs.kotlinxSerializationJson)
            koinAndroidDependencies(true)
            unitTestingBundle
        }
    }

}
