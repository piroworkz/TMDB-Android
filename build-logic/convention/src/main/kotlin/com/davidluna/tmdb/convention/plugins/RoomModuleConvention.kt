package com.davidluna.tmdb.convention.plugins

import androidx.room.gradle.RoomExtension
import com.davidluna.tmdb.convention.bundles.dataHttpClientDependencies
import com.davidluna.tmdb.convention.bundles.koinAndroidDependencies
import com.davidluna.tmdb.convention.bundles.kotlinLibsDependencies
import com.davidluna.tmdb.convention.bundles.roomDependencies
import com.davidluna.tmdb.convention.bundles.unitTestingBundle
import com.davidluna.tmdb.convention.extensions.android_library.androidLibrary
import com.davidluna.tmdb.convention.extensions.common.roomPluginManger
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.ksp
import com.davidluna.tmdb.convention.helpers.libs
import com.davidluna.tmdb.convention.helpers.roomCompiler
import com.davidluna.tmdb.convention.helpers.roomKtx
import com.davidluna.tmdb.convention.helpers.roomRuntime
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
            dataHttpClientDependencies()
            koinAndroidDependencies()
            kotlinLibsDependencies()
            roomDependencies()
            unitTestingBundle
        }
    }

    private fun Project.room(configure: Action<RoomExtension>) {
        (this as ExtensionAware).extensions.configure("room", configure)
    }
}