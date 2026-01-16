package com.davidluna.tmdb.core_data.di

import com.davidluna.tmdb.core_data.framework.location.AndroidLocationProvider
import com.davidluna.tmdb.core_data.framework.location.AndroidLocationService
import com.davidluna.tmdb.core_data.framework.location.CountryCodeResolver
import com.davidluna.tmdb.core_data.framework.location.GeoCountryCodeResolver
import com.davidluna.tmdb.core_data.framework.location.LocationIsPermissionValid
import com.davidluna.tmdb.core_data.framework.location.LocationService
import com.davidluna.tmdb.core_data.framework.messaging.NotificationChannelInstaller
import com.davidluna.tmdb.core_data.framework.messaging.TmdbNotificationChannelStateReader
import com.davidluna.tmdb.core_data.framework.messaging.TmdbNotificationsManager
import com.davidluna.tmdb.core_domain.usecases.InstallNotificationChannels
import com.davidluna.tmdb.core_domain.usecases.IsChannelEnabled
import com.davidluna.tmdb.core_domain.usecases.IsPermissionValid
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.core_domain.usecases.ShowNotification
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkLocationModule = module {
    factoryOf(::AndroidLocationProvider) bind ObserveCountryCode::class
    factoryOf(::AndroidLocationService) bind LocationService::class
    factoryOf(::GeoCountryCodeResolver) bind CountryCodeResolver::class
    factoryOf(::LocationIsPermissionValid) bind IsPermissionValid::class
}

val frameworkNotificationModule = module {
    factoryOf(::TmdbNotificationsManager) bind ShowNotification::class
    factoryOf(::TmdbNotificationChannelStateReader) bind IsChannelEnabled::class
    factoryOf(::NotificationChannelInstaller) bind InstallNotificationChannels::class
}