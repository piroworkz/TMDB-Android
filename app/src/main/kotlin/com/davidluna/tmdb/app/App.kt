package com.davidluna.tmdb.app

import android.app.Application
import com.davidluna.tmdb.app.di.startDi
import com.davidluna.tmdb.core_domain.usecases.InstallNotificationChannels
import org.koin.android.ext.android.get


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startDi()
        get<InstallNotificationChannels>()()
    }
}