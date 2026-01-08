package com.davidluna.tmdb.core_data.di

import android.app.Application
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val locationModule = module {
    singleOf(::provideFusedLocationProviderClient)
    singleOf(::provideGeoCoder)
}

private fun provideFusedLocationProviderClient(application: Application) =
    LocationServices.getFusedLocationProviderClient(application)

private fun provideGeoCoder(application: Application) =
    Geocoder(application)