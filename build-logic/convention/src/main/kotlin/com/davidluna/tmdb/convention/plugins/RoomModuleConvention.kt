package com.davidluna.tmdb.convention.plugins

import androidx.room.gradle.RoomExtension
import com.davidluna.tmdb.convention.bundles.unitTestingBundle
import com.davidluna.tmdb.convention.extensions.android_library.androidLibrary
import com.davidluna.tmdb.convention.extensions.common.roomPluginManger
import com.davidluna.tmdb.convention.extensions.libs.arrowCore
import com.davidluna.tmdb.convention.extensions.libs.kotlinCoroutinesCore
import com.davidluna.tmdb.convention.extensions.libs.kotlinxSerializationJson
import com.davidluna.tmdb.convention.extensions.libs.libs
import com.davidluna.tmdb.convention.extensions.libs.retrofit
import com.davidluna.tmdb.convention.extensions.libs.roomCompiler
import com.davidluna.tmdb.convention.extensions.libs.roomKtx
import com.davidluna.tmdb.convention.extensions.libs.roomRuntime
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.ksp
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class RoomModuleConvention : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        roomPluginManger
        androidLibrary
        dependencies()
        room {
            schemaDirectory("$projectDir/schemas")
        }

    }

    private fun Project.dependencies() {
        dependencies {
            implementation(libs.roomRuntime)
            implementation(libs.roomKtx)
            ksp(libs.roomCompiler)
            implementation(libs.retrofit)
            implementation(libs.arrowCore)
            implementation(libs.kotlinxSerializationJson)
            implementation(libs.kotlinCoroutinesCore)
            unitTestingBundle
        }
    }

    private fun Project.room(configure: Action<RoomExtension>) {
        (this as ExtensionAware).extensions.configure("room", configure)
    }
}