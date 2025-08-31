package com.davidluna.tmdb.media_framework.di

import android.app.Application
import androidx.room.Room
import com.davidluna.tmdb.media_framework.data.local.database.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MediaDataBaseModule::class]
)
object TestMediaDataBaseModule {

    @Singleton
    @Provides
    fun provideMediaDatabase(app: Application): MediaDatabase =
        Room.inMemoryDatabaseBuilder(app, MediaDatabase::class.java).build()
}