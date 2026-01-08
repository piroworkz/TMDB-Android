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
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkLocationModule = module {
    singleOf(::AndroidLocationProvider) bind ObserveCountryCode::class
    singleOf(::AndroidLocationService) bind LocationService::class
    singleOf(::GeoCountryCodeResolver) bind CountryCodeResolver::class
    singleOf(::LocationIsPermissionValid) bind IsPermissionValid::class
}

val frameworkNotificationModule = module {
    singleOf(::TmdbNotificationsManager) bind ShowNotification::class
    singleOf(::TmdbNotificationChannelStateReader) bind IsChannelEnabled::class
    singleOf(::NotificationChannelInstaller) bind InstallNotificationChannels::class
}