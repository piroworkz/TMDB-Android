import com.davidluna.tmdb.convention.constants.Constants

plugins {
    alias(libs.plugins.frameworkModuleConventionPlugin)
    alias(libs.plugins.googleServices)
}

android {
    namespace = Constants.NAMESPACE.plus(".core_data")

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
dependencies {
    implementation(libs.coreDatastore)
    implementation(libs.firebaseMessaging)
    implementation(libs.okhttpClient)
    implementation(libs.playServicesLocation)
    implementation(platform(libs.firebaseBom))
    implementation(projects.feature.core.coreDomain)
}