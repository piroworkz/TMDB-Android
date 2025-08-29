plugins {
    alias(libs.plugins.architectCodersAndroidApplication)
}

dependencies {
    implementation(libs.core.splashscreen)
    implementation(projects.feature.core.coreUi)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreFramework)
    implementation(projects.feature.auth.authUi)
    implementation(projects.feature.auth.authDomain)
    implementation(projects.feature.auth.authFramework)
    implementation(projects.feature.media.mediaUi)
    implementation(projects.feature.media.mediaDomain)
    implementation(projects.feature.media.mediaFramework)
    testImplementation(projects.testShared)
    testImplementation(testFixtures(projects.feature.auth.authFramework))
    testImplementation(libs.coreDatastore)
    androidTestImplementation(libs.navigationTesting)
    androidTestImplementation(projects.testShared)
}