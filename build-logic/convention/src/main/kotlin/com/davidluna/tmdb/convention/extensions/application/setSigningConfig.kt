package com.davidluna.tmdb.convention.extensions.application

import com.android.build.api.dsl.ApplicationExtension
import com.davidluna.tmdb.convention.constants.Constants.KEY_ALIAS
import com.davidluna.tmdb.convention.constants.Constants.KEY_PASSWORD
import com.davidluna.tmdb.convention.constants.Constants.STORE_FILE
import com.davidluna.tmdb.convention.constants.Constants.STORE_PASSWORD
import org.gradle.api.Project

fun Project.setSigningConfig(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.signingConfigs {
        create("release") {
            keyAlias =
                System.getenv(KEY_ALIAS) ?: project.findProperty(KEY_ALIAS) as? String
            keyPassword =
                System.getenv(KEY_PASSWORD) ?: project.findProperty(KEY_PASSWORD) as? String
            (System.getenv(STORE_FILE) ?: project.findProperty(STORE_FILE) as? String)?.let {
                storeFile = file(it)
                storePassword = System.getenv(STORE_PASSWORD) ?: project.findProperty(STORE_PASSWORD) as? String
            }
        }
    }
}