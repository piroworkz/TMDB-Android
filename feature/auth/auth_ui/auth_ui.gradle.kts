import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.uiModuleConventionPlugin)
}

android {
    namespace = Constants.NAMESPACE.plus(".auth_ui")
}

dependencies {
    implementation(libs.biometric)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreUi)
    implementation(projects.feature.auth.authDomain)
    testImplementation(projects.feature.auth.authFramework)
    testImplementation(projects.feature.core.coreFramework)
    testImplementation(projects.testShared)
    testImplementation(testFixtures(projects.feature.auth.authFramework))
}