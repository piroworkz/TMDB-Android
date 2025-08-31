package com.davidluna.tmdb.media_framework.di

import android.app.Application
import androidx.room.Room
import com.davidluna.tmdb.media_framework.data.local.database.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaDataBaseModule {

    @Singleton
    @Provides
    fun provideMediaDatabase(app: Application): MediaDatabase =
        Room.databaseBuilder(app, MediaDatabase::class.java, "media.db").build()
}