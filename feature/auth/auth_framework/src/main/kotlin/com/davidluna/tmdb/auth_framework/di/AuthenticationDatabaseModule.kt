package com.davidluna.tmdb.auth_framework.di

import android.app.Application
import androidx.room.Room
import com.davidluna.tmdb.auth_framework.data.local.database.AuthenticationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationDatabaseModule {
    @Singleton
    @Provides
    fun provideAuthenticationDatabase(app: Application): AuthenticationDatabase =
        Room.databaseBuilder(app, AuthenticationDatabase::class.java, "authentication.db").build()
}