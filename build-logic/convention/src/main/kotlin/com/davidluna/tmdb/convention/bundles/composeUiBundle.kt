package com.davidluna.tmdb.convention.bundles

import com.davidluna.tmdb.convention.helpers.composeAnimation
import com.davidluna.tmdb.convention.helpers.composeBom
import com.davidluna.tmdb.convention.helpers.composeMaterial3
import com.davidluna.tmdb.convention.helpers.composeNavigation
import com.davidluna.tmdb.convention.helpers.composeUi
import com.davidluna.tmdb.convention.helpers.composeUiGraphics
import com.davidluna.tmdb.convention.helpers.composeUiTooling
import com.davidluna.tmdb.convention.helpers.composeUiToolingPreview
import com.davidluna.tmdb.convention.helpers.debugImplementation
import com.davidluna.tmdb.convention.helpers.iconsExtended
import com.davidluna.tmdb.convention.helpers.implementation
import com.davidluna.tmdb.convention.helpers.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal val Project.composeUiBundle: Unit
    get() {
        dependencies {
            implementation(platform(libs.composeBom))
            implementation(libs.composeUi)
            implementation(libs.composeUiGraphics)
            implementation(libs.composeUiToolingPreview)
            implementation(libs.composeMaterial3)
            implementation(libs.composeNavigation)
            implementation(libs.composeAnimation)
            implementation(libs.iconsExtended)
            debugImplementation(libs.composeUiTooling)
        }
    }