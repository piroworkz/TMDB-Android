package com.davidluna.tmdb.core_framework.di

import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.core_domain.usecases.InstallNotificationChannels
import com.davidluna.tmdb.core_domain.usecases.IsChannelEnabled
import com.davidluna.tmdb.core_domain.usecases.IsPermissionValid
import com.davidluna.tmdb.core_domain.usecases.ShowNotification
import com.davidluna.tmdb.core_framework.data.local.sources.AndroidLocationProvider
import com.davidluna.tmdb.core_framework.data.local.sources.AndroidLocationService
import com.davidluna.tmdb.core_framework.data.local.sources.CountryCodeResolver
import com.davidluna.tmdb.core_framework.data.local.sources.GeoCountryCodeResolver
import com.davidluna.tmdb.core_framework.data.local.sources.LocationIsPermissionValid
import com.davidluna.tmdb.core_framework.data.local.sources.LocationService
import com.davidluna.tmdb.core_framework.data.remote.messaging.NotificationChannelInstaller
import com.davidluna.tmdb.core_framework.data.remote.messaging.TmdbNotificationChannelStateReader
import com.davidluna.tmdb.core_framework.data.remote.messaging.TmdbNotificationsManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreDataModule {
    @Binds
    abstract fun bindCountryCodeResolver(source: GeoCountryCodeResolver): CountryCodeResolver
    @Binds
    abstract fun bindGetCountryCode(source: AndroidLocationProvider): ObserveCountryCode
    @Binds
    abstract fun bindLocationService(source: AndroidLocationService): LocationService
    @Binds
    abstract fun bindPermissionValidator(source: LocationIsPermissionValid): IsPermissionValid
    @Binds
    abstract fun bindShowNotification(source: TmdbNotificationsManager): ShowNotification
    @Binds
    abstract fun bindInstallNotificationChannels(source: NotificationChannelInstaller): InstallNotificationChannels
    @Binds
    abstract fun bindIsChannelEnabled(source: TmdbNotificationChannelStateReader): IsChannelEnabled
}