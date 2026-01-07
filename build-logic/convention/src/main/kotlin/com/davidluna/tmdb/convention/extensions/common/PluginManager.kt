package com.davidluna.tmdb.convention.extensions.common

import com.davidluna.tmdb.convention.helpers.alias
import com.davidluna.tmdb.convention.helpers.androidApplication
import com.davidluna.tmdb.convention.helpers.androidLibrary
import com.davidluna.tmdb.convention.helpers.composeCompiler
import com.davidluna.tmdb.convention.helpers.googleServices
import com.davidluna.tmdb.convention.helpers.kotlinAndroid
import com.davidluna.tmdb.convention.helpers.kotlinSerialization
import com.davidluna.tmdb.convention.helpers.ksp
import com.davidluna.tmdb.convention.helpers.libs
import com.davidluna.tmdb.convention.helpers.roomPlugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager

internal val Project.applicationPluginManager: PluginManager
    get() = pluginManager.apply {
        alias(libs.ksp)
        alias(libs.androidApplication)
        alias(libs.kotlinAndroid)
        alias(libs.composeCompiler)
        alias(libs.googleServices)
    }

val Project.frameworkPluginManager: PluginManager
    get() = pluginManager.apply {
        alias(libs.ksp)
        alias(libs.androidLibrary)
        alias(libs.kotlinAndroid)
        alias(libs.kotlinSerialization)
    }

internal val Project.uiPluginManager: PluginManager
    get() = frameworkPluginManager.apply {
        alias(libs.composeCompiler)
    }

internal val Project.roomPluginManger: PluginManager
    get() = frameworkPluginManager.apply {
        alias(libs.roomPlugin)
    }
