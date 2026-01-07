package com.davidluna.tmdb.convention.plugins

import com.davidluna.tmdb.convention.bundles.androidTestingBundle
import com.davidluna.tmdb.convention.bundles.composeUiBundle
import com.davidluna.tmdb.convention.bundles.koinAndroidDependencies
import com.davidluna.tmdb.convention.bundles.kotlinLibsDependencies
import com.davidluna.tmdb.convention.bundles.unitTestingBundle
import com.davidluna.tmdb.convention.extensions.android_library.androidLibrary
import com.davidluna.tmdb.convention.extensions.common.uiPluginManager
import com.davidluna.tmdb.convention.helpers.activityKtx
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class UiModuleConventionPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        uiPluginManager
        androidLibrary
        dependencies()
    }

    private fun Project.dependencies() {
        dependencies {
            androidTestingBundle
            composeUiBundle
            implementation(libs.activityKtx)
            kotlinLibsDependencies()
            koinAndroidDependencies(true)
            unitTestingBundle
        }
    }
}