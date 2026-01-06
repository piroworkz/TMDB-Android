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
    implementation(projects.feature.auth.authDomain)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreData)
    testFixturesImplementation(libs.arrowCore)
    testFixturesImplementation(libs.coroutinesTest)
    testFixturesImplementation(libs.kotlinStdLib)
    testFixturesImplementation(projects.feature.auth.authDomain)
    testFixturesImplementation(projects.feature.core.coreDomain)
    testFixturesImplementation(projects.feature.core.coreData)
    testFixturesImplementation(projects.testShared)
    testImplementation(projects.testShared)
}