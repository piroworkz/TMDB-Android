package com.davidluna.tmdb.convention.plugins

import com.davidluna.tmdb.convention.bundles.dataHttpClientDependencies
import com.davidluna.tmdb.convention.bundles.koinAndroidDependencies
import com.davidluna.tmdb.convention.bundles.kotlinLibsDependencies
import com.davidluna.tmdb.convention.bundles.unitTestingBundle
import com.davidluna.tmdb.convention.extensions.android_library.androidLibrary
import com.davidluna.tmdb.convention.extensions.common.frameworkPluginManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

@Suppress("unused")
class FrameworkModuleConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        frameworkPluginManager
        androidLibrary
        dependencies()
        dynamicAgentLoading()
    }

    private fun Project.dependencies() {
        dependencies {
            dataHttpClientDependencies()
            koinAndroidDependencies()
            kotlinLibsDependencies()
            unitTestingBundle
        }
    }
}

fun Project.dynamicAgentLoading() {
    tasks.withType<Test>().configureEach {
        jvmArgs("-XX:+EnableDynamicAgentLoading")
    }
}

