import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.uiModuleConventionPlugin)
}

android {
    namespace = Constants.NAMESPACE.plus(".media_ui")
}

dependencies {
    implementation(projects.feature.core.coreUi)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.media.mediaDomain)
    implementation(libs.coilCompose)
    implementation(libs.pagingRuntime)
    implementation(libs.pagingCompose)
    testImplementation(projects.testShared)
    testImplementation(projects.feature.core.coreFramework)
    testImplementation(testFixtures(projects.feature.media.mediaFramework))
    testImplementation(libs.coreDatastore)
    testImplementation(libs.pagingTesting)
}