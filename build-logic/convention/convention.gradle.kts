plugins {
    `kotlin-dsl`
//    alias(libs.plugins.ksp)
}

group = "com.davidluna.tmdb"

dependencies {
    compileOnly(libs.androidGradlePlugin)
    compileOnly(libs.kotlinGradlePlugin)
    compileOnly(libs.roomGradlePlugin)
//    ksp(libs.piroworkzVersionsCatalog)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

//ksp {
//    arg("libsPath", "${rootDir.parentFile}/gradle/libs.versions.toml")
//    arg("packageName", "com.davidluna.tmdb.convention.helpers.libs")
//}

gradlePlugin {

    plugins {
        register("androidAppPlugin") {
            id = "tmdb.android.application"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.AndroidApplicationConventionPlugin"
        }

        register("frameworkModuleConventionPlugin") {
            id = "tmdb.framework.module.plugin"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.FrameworkModuleConventionPlugin"
        }

        register("uiModuleConventionPlugin") {
            id = "tmdb.ui.module.plugin"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.UiModuleConventionPlugin"
        }

        register("kotlinModuleConventionPlugin") {
            id = "tmdb.kotlin.module.plugin"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.KotlinModuleConventionPlugin"
        }

        register("testSharedConventionPlugin") {
            id = "tmdb.test.shared.plugin"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.TestSharedConventionPlugin"
        }

        register("roomModuleConventionPlugin") {
            id = "tmdb.room.module.plugin"
            implementationClass =
                "com.davidluna.tmdb.convention.plugins.RoomModuleConvention"
        }
    }
}
