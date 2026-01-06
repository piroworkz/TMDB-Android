package com.davidluna.tmdb.core_data.di

import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.core_domain.usecases.InstallNotificationChannels
import com.davidluna.tmdb.core_domain.usecases.IsChannelEnabled
import com.davidluna.tmdb.core_domain.usecases.IsPermissionValid
import com.davidluna.tmdb.core_domain.usecases.ShowNotification
import com.davidluna.tmdb.core_data.framework.location.AndroidLocationProvider
import com.davidluna.tmdb.core_data.framework.location.AndroidLocationService
import com.davidluna.tmdb.core_data.framework.location.CountryCodeResolver
import com.davidluna.tmdb.core_data.framework.location.GeoCountryCodeResolver
import com.davidluna.tmdb.core_data.framework.location.LocationIsPermissionValid
import com.davidluna.tmdb.core_data.framework.location.LocationService
import com.davidluna.tmdb.core_data.framework.messaging.NotificationChannelInstaller
import com.davidluna.tmdb.core_data.framework.messaging.TmdbNotificationChannelStateReader
import com.davidluna.tmdb.core_data.framework.messaging.TmdbNotificationsManager
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