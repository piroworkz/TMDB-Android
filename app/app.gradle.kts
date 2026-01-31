plugins {
    alias(libs.plugins.architectCodersAndroidApplication)
}

dependencies {
    androidTestImplementation(libs.navigationTesting)
    androidTestImplementation(projects.testShared)
    implementation(libs.core.splashscreen)
    implementation(projects.feature.auth.authData)
    implementation(projects.feature.auth.authDomain)
    implementation(projects.feature.auth.authUi)
    implementation(projects.feature.core.coreData)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreUi)
    implementation(projects.feature.media.mediaData)
    implementation(projects.feature.media.mediaDomain)
    implementation(projects.feature.media.mediaUi)
    testImplementation(libs.coreDatastore)
    testImplementation(libs.koinTest)
    testImplementation(platform(libs.koinBom))
    testImplementation(projects.testShared)
    testImplementation(testFixtures(projects.feature.auth.authData))
    testImplementation(testFixtures(projects.feature.media.mediaData))
}