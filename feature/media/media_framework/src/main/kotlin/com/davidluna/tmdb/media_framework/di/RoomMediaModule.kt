package com.davidluna.tmdb.media_framework.di

import com.davidluna.tmdb.media_framework.data.local.database.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomMediaModule {
    @Singleton
    @Provides
    fun provideMediaDao(db: MediaDatabase) = db.mediaDao

    @Singleton
    @Provides
    fun provideMediaDetailsDao(db: MediaDatabase) = db.mediaDetailsDao

    @Singleton
    @Provides
    fun provideRemoteKeysDao(db: MediaDatabase) = db.remoteKeysDao

    @Singleton
    @Provides
    fun provideVideosDao(db: MediaDatabase) = db.videosDao
}

