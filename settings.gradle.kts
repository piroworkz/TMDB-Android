@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Tmdb2024"

include(
    ":app",
    ":feature:auth:auth_ui",
    ":feature:auth:auth_domain",
    ":feature:auth:auth_data",
    ":feature:media:media_ui",
    ":feature:media:media_domain",
    ":feature:media:media_data",
    ":feature:core:core_ui",
    ":feature:core:core_domain",
    ":feature:core:core_data",
    ":test_shared"
)

setProjectBuildFileName(rootProject)

fun setProjectBuildFileName(project: ProjectDescriptor) {
    project.buildFileName = "${project.name}.gradle.kts"
    project.children.forEach { child ->
        setProjectBuildFileName(child)
    }
}