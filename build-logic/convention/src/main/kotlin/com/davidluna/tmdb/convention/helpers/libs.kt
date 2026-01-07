package com.davidluna.tmdb.convention.helpers

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

internal val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
internal val VersionCatalog.activityKtx: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("activityKtx").get()
internal val VersionCatalog.androidApplication: Provider<PluginDependency>
    get() = findPlugin("androidApplication").get()
internal val VersionCatalog.androidGradlePlugin: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidGradlePlugin").get()
internal val VersionCatalog.androidLibrary: Provider<PluginDependency>
    get() = findPlugin("androidLibrary").get()
internal val VersionCatalog.androidRunner: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidRunner").get()
internal val VersionCatalog.androidTestCore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidTestCore").get()
internal val VersionCatalog.androidTestRules: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("androidTestRules").get()
internal val VersionCatalog.appcompat: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("appcompat").get()
internal val VersionCatalog.arrowCore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("arrowCore").get()
internal val VersionCatalog.biometric: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("biometric").get()
internal val VersionCatalog.coilCompose: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("coilCompose").get()
internal val VersionCatalog.composeActivity: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeActivity").get()
internal val VersionCatalog.composeAnimation: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeAnimation").get()
internal val VersionCatalog.composeBom: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeBom").get()
internal val VersionCatalog.composeCompiler: Provider<PluginDependency>
    get() = findPlugin("composeCompiler").get()
internal val VersionCatalog.composeMaterial3: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeMaterial3").get()
internal val VersionCatalog.composeNavigation: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeNavigation").get()
internal val VersionCatalog.composeRuntime: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeRuntime").get()
internal val VersionCatalog.composeUi: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeUi").get()
internal val VersionCatalog.composeUiGraphics: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeUiGraphics").get()
internal val VersionCatalog.composeUiTooling: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeUiTooling").get()
internal val VersionCatalog.composeUiToolingPreview: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("composeUiToolingPreview").get()
internal val VersionCatalog.coreDatastore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("coreDatastore").get()
internal val VersionCatalog.coreKtx: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("core.ktx").get()
internal val VersionCatalog.coreSplashscreen: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("core.splashscreen").get()
internal val VersionCatalog.coroutinesTest: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("coroutinesTest").get()
internal val VersionCatalog.espressoCore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("espresso.core").get()
internal val VersionCatalog.extJunit: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("ext.junit").get()
internal val VersionCatalog.firebaseAnalytics: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("firebaseAnalytics").get()
internal val VersionCatalog.firebaseBom: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("firebaseBom").get()
internal val VersionCatalog.firebaseCrashlytics: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("firebaseCrashlytics").get()
internal val VersionCatalog.firebaseMessaging: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("firebaseMessaging").get()
internal val VersionCatalog.firebasePerformance: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("firebasePerformance").get()
internal val VersionCatalog.googleServices: Provider<PluginDependency>
    get() = findPlugin("googleServices").get()
internal val VersionCatalog.iconsExtended: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("iconsExtended").get()
internal val VersionCatalog.junit: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("junit").get()
internal val VersionCatalog.koinAndroid: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinAndroid").get()
internal val VersionCatalog.koinAnnotations: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinAnnotations").get()
internal val VersionCatalog.koinBom: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinBom").get()
internal val VersionCatalog.koinCompose: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinCompose").get()
internal val VersionCatalog.koinComposeNavigation: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinComposeNavigation").get()
internal val VersionCatalog.koinCore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinCore").get()
internal val VersionCatalog.koinKspCompiler: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("koinKspCompiler").get()
internal val VersionCatalog.kotlinAndroid: Provider<PluginDependency>
    get() = findPlugin("kotlinAndroid").get()
internal val VersionCatalog.kotlinConverter: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinConverter").get()
internal val VersionCatalog.kotlinCoroutinesCore: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinCoroutinesCore").get()
internal val VersionCatalog.kotlinDsl: Provider<PluginDependency>
    get() = findPlugin("kotlinDsl").get()
internal val VersionCatalog.kotlinGradlePlugin: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinGradlePlugin").get()
internal val VersionCatalog.kotlinJvm: Provider<PluginDependency>
    get() = findPlugin("kotlinJvm").get()
internal val VersionCatalog.kotlinSerialization: Provider<PluginDependency>
    get() = findPlugin("kotlinSerialization").get()
internal val VersionCatalog.kotlinStdLib: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinStdLib").get()
internal val VersionCatalog.kotlinxDatetime: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinx.datetime").get()
internal val VersionCatalog.kotlinxSerializationJson: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("kotlinxSerializationJson").get()
internal val VersionCatalog.ksp: Provider<PluginDependency>
    get() = findPlugin("ksp").get()
internal val VersionCatalog.material: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("material").get()
internal val VersionCatalog.mockKAndroid: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("mockKAndroid").get()
internal val VersionCatalog.mockWebServer: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("mockWebServer").get()
internal val VersionCatalog.mockk: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("mockk").get()
internal val VersionCatalog.navigationTesting: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("navigationTesting").get()
internal val VersionCatalog.okhttpClient: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("okhttpClient").get()
internal val VersionCatalog.okhttpLoggingInterceptor: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("okhttpLoggingInterceptor").get()
internal val VersionCatalog.pagingCompose: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("pagingCompose").get()
internal val VersionCatalog.pagingJVM: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("pagingJVM").get()
internal val VersionCatalog.pagingRuntime: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("pagingRuntime").get()
internal val VersionCatalog.pagingTesting: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("pagingTesting").get()
internal val VersionCatalog.playServicesLocation: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("playServicesLocation").get()
internal val VersionCatalog.retrofit: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("retrofit").get()
internal val VersionCatalog.roomCompiler: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("roomCompiler").get()
internal val VersionCatalog.roomGradlePlugin: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("roomGradlePlugin").get()
internal val VersionCatalog.roomKtx: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("roomKtx").get()
internal val VersionCatalog.roomPaging: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("roomPaging").get()
internal val VersionCatalog.roomPlugin: Provider<PluginDependency>
    get() = findPlugin("roomPlugin").get()
internal val VersionCatalog.roomRuntime: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("roomRuntime").get()
internal val VersionCatalog.turbine: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("turbine").get()
internal val VersionCatalog.uiTestJunit4: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("uiTestJunit4").get()
internal val VersionCatalog.uiTestManifest: Provider<MinimalExternalModuleDependency>
    get() = findLibrary("uiTestManifest").get()