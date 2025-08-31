package com.davidluna.tmdb.core_framework.di

import android.app.Application
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(application: Application) =
        LocationServices.getFusedLocationProviderClient(application)

    @Singleton
    @Provides
    fun provideGeoCoder(application: Application) =
        Geocoder(application)
}

