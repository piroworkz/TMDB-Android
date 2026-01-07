package com.davidluna.tmdb.convention.plugins

import com.davidluna.tmdb.convention.bundles.kotlinLibsDependencies
import com.davidluna.tmdb.convention.constants.Constants
import com.davidluna.tmdb.convention.helpers.alias
import com.davidluna.tmdb.convention.helpers.java
import com.davidluna.tmdb.convention.helpers.kotlinJvm
import com.davidluna.tmdb.convention.helpers.libs
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

@Suppress("unused")
class KotlinModuleConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        applyPlugins()
        setUpJavaVersions()
        setupDependencies()
        kotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
                freeCompilerArgs.add("-Xcontext-sensitive-resolution")
            }
        }
    }

    private fun Project.applyPlugins() {
        pluginManager.apply {
            alias(libs.kotlinJvm)
        }
    }

    private fun Project.setUpJavaVersions() {
        java {
            sourceCompatibility = Constants.JAVA_VERSION
            targetCompatibility = Constants.JAVA_VERSION
        }
    }

    private fun Project.kotlin(config: Action<KotlinJvmProjectExtension>) {
        (this as ExtensionAware).extensions.configure("kotlin", config)
    }

    private fun Project.setupDependencies() {
        dependencies {
            kotlinLibsDependencies()
        }
    }
}