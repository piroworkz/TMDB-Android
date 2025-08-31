package com.davidluna.tmdb.auth_framework.di

import android.content.Context
import androidx.room.Room
import com.davidluna.tmdb.auth_framework.data.local.database.AuthenticationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthenticationDatabaseModule::class]
)
object TestAuthenticationDatabaseModule {
    @Singleton
    @Provides
    fun provideAuthenticationDatabase(context: Context): AuthenticationDatabase =
        Room.inMemoryDatabaseBuilder(context, AuthenticationDatabase::class.java).build()
}