package com.davidluna.tmdb.app.di

import com.davidluna.tmdb.BuildConfig
import com.davidluna.tmdb.app.App
import com.davidluna.tmdb.app.main_ui.presenter.MainViewModel
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
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun App.startDi() {
    startKoin {
        if (BuildConfig.DEBUG) {
            androidLogger(Level.ERROR)
        }
        androidContext(this@startDi)
        modules(
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
}