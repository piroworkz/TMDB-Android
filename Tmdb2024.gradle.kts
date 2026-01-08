plugins {
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.roomPlugin) apply false
    alias(libs.plugins.googleServices) apply false
}

tasks.register("connectedUiTests") {
    group = "verification"
    description = "Runs all Android instrumented tests."
    dependsOn(
        subprojects
            .filter { it.name.endsWith("ui") || it.name == "app" }
            .mapNotNull { it.tasks.findByName("connectedDebugAndroidTest") }

    )
}

tasks.register<Copy>("aggregateUiAndroidTestReports") {
    group = "verification"
    description = "Collects all Android test reports into a single folder."
    val dependencies = subprojects
        .filter { it.name.endsWith("ui") || it.name == "app" }
        .mapNotNull { it.tasks.findByName("connectedDebugAndroidTest") }
    dependsOn(dependencies)
    from(
        subprojects
            .filter { it.name.endsWith("ui") || it.name == "app" }
            .map { it.layout.buildDirectory.dir("reports/androidTests/connected") }
    )
    into(layout.buildDirectory.dir("reports/connectedUiTests"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}