package com.davidluna.tmdb.app.main_ui.presenter

import com.davidluna.tmdb.app.di.networkModule
import com.davidluna.tmdb.auth_data.di.authDataModule
import com.davidluna.tmdb.auth_data.di.authFrameworkLocalModule
import com.davidluna.tmdb.auth_data.di.authFrameworkRemoteModule
import com.davidluna.tmdb.auth_ui.di.authPresentationModule
import com.davidluna.tmdb.core_data.di.coroutinesModule
import com.davidluna.tmdb.core_data.di.dataNotificationsModule
import com.davidluna.tmdb.core_data.di.dataStoreModule
import com.davidluna.tmdb.core_data.di.frameworkLocationModule
import com.davidluna.tmdb.core_data.di.frameworkNotificationModule
import com.davidluna.tmdb.core_data.di.locationModule
import com.davidluna.tmdb.core_data.di.nativeModule
import com.davidluna.tmdb.core_ui.di.notificationChannelsModule
import com.davidluna.tmdb.media_data.di.mediaFrameworkLocalModule
import com.davidluna.tmdb.media_data.di.mediaFrameworkRemoteModule
import com.davidluna.tmdb.media_ui.di.mediaPresentationModule
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class KoinModuleTest : KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyKoinApp() {
        val appModule = module {
            includes(
                module { viewModelOf(::MainViewModel) },
                authDataModule,
                authFrameworkLocalModule,
                authFrameworkRemoteModule,
                authPresentationModule,
                coroutinesModule,
                dataNotificationsModule,
                dataStoreModule,
                frameworkLocationModule,
                networkModule,
                frameworkNotificationModule,
                locationModule,
                mediaFrameworkLocalModule,
                mediaFrameworkRemoteModule,
                nativeModule,
                notificationChannelsModule,
                mediaPresentationModule
            )
        }
        appModule.verify(
            extraTypes = listOf(
                android.app.Application::class,
                android.content.Context::class,
                java.util.Locale::class
            )
        )
    }
}