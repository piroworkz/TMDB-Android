import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.roomModuleConventionPlugin)
}

android {
    namespace = Constants.NAMESPACE.plus(".media_framework")

    @Suppress("UnstableApiUsage")
    testFixtures {
        enable = true
    }
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.roomPaging)
    implementation(libs.coreDatastore)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreFramework)
    implementation(projects.feature.media.mediaDomain)
    implementation(libs.pagingJVM)
    testImplementation(projects.testShared)
    testImplementation(libs.pagingTesting)
    testFixturesImplementation(projects.testShared)
    testFixturesImplementation(projects.feature.media.mediaDomain)
    testFixturesImplementation(libs.arrowCore)
    testFixturesImplementation(libs.coroutinesTest)
    testFixturesImplementation(libs.kotlinStdLib)
    testFixturesImplementation(libs.pagingJVM)
    testFixturesImplementation(projects.feature.core.coreFramework)
    testFixturesImplementation(projects.feature.core.coreDomain)

}