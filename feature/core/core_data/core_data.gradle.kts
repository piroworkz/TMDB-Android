import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.frameworkModuleConventionPlugin)

}

android {
    namespace = Constants.NAMESPACE.plus(".core_framework")

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
dependencies {
    implementation(projects.feature.core.coreDomain)
    implementation(libs.playServicesLocation)
    implementation(libs.okhttpClient)
    implementation(libs.coreDatastore)
    implementation(libs.firebaseMessaging)
}