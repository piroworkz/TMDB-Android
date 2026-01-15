package com.davidluna.tmdb.convention.helpers

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

internal fun Project.androidLibrary(config: Action<LibraryExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", config)

internal fun Project.androidApplication(config: Action<ApplicationExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", config)

internal fun Project.android(config: Action<ApplicationExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", config)