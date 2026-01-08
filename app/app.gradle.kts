plugins {
    alias(libs.plugins.architectCodersAndroidApplication)
}



dependencies {
    implementation(libs.core.splashscreen)
    implementation(projects.feature.core.coreUi)
    implementation(projects.feature.core.coreDomain)
    implementation(projects.feature.core.coreData)
    implementation(projects.feature.auth.authUi)
    implementation(projects.feature.auth.authDomain)
    implementation(projects.feature.auth.authData)
    implementation(projects.feature.media.mediaUi)
    implementation(projects.feature.media.mediaDomain)
    implementation(projects.feature.media.mediaData)
    testImplementation(projects.testShared)
    testImplementation(testFixtures(projects.feature.auth.authData))
    testImplementation(libs.coreDatastore)
    testImplementation(platform(libs.koinBom))
    testImplementation(libs.koinTest)
    androidTestImplementation(libs.navigationTesting)
    androidTestImplementation(projects.testShared)
}