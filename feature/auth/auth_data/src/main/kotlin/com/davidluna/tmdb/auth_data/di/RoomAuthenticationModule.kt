package com.davidluna.tmdb.auth_data.di

import com.davidluna.tmdb.auth_data.framework.local.database.AuthenticationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomAuthenticationModule {

    @Singleton
    @Provides
    fun provideAccountDao(db: AuthenticationDatabase) = db.accountDao

    @Singleton
    @Provides
    fun provideSessionDao(db: AuthenticationDatabase) = db.sessionDao
}

