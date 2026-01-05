plugins {
    alias(libs.plugins.kotlinModuleConventionPlugin)
}

dependencies {
    implementation(projects.feature.core.coreDomain)
    implementation(libs.pagingJVM)
    testImplementation(libs.junit)
}
