import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.roomModuleConventionPlugin)
}


android {
    namespace = Constants.NAMESPACE.plus(".auth_framework")

    @Suppress("UnstableApiUsage")
    testFixtures {
        enable = true
    }
}
dependencies {
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreFramework)
    implementation(projects.feature.auth.authDomain)
    testImplementation(projects.testShared)
    testFixturesImplementation(projects.testShared)
    testFixturesImplementation(projects.feature.core.coreFramework)
    testFixturesImplementation(libs.arrowCore)
    testFixturesImplementation(libs.coroutinesTest)
    testFixturesImplementation(libs.kotlinStdLib)
}