package com.davidluna.tmdb.media_data.di

import android.app.Application
import androidx.room.Room
import com.davidluna.tmdb.media_data.framework.local.database.MediaDatabase
import com.davidluna.tmdb.media_data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_data.framework.paging.CachePolicyValidator
import com.davidluna.tmdb.media_data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.repositories.FavoritesRepository
import com.davidluna.tmdb.media_domain.usecases.ClearFavorites
import com.davidluna.tmdb.media_domain.usecases.ObserveFavoriteMedia
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mediaFrameworkLocalModule = module {
    factoryOf(::CachePolicyValidator) bind IsCacheExpired::class
    factoryOf(::CachePolicyValidator) bind IsCacheExpired::class
    factoryOf(::SelectedCatalogDataSource) bind ObserveSelectedMediaCatalog::class
    factoryOf(::SelectedCatalogDataSource) bind UpdateSelectedEndpoint::class
    factoryOf(::FavoritesRepository) bind ObserveFavoriteMedia::class
    factoryOf(::FavoritesRepository) bind ToggleFavorite::class
    factoryOf(::FavoritesRepository) bind ClearFavorites::class
    singleOf(::provideMediaDao)
    singleOf(::provideFavoritesDao)
    singleOf(::provideMediaDetailsDao)
    singleOf(::provideRemoteKeysDao)
    singleOf(::provideVideosDao)
    singleOf(::provideMediaDatabase)
}

private fun provideMediaDatabase(app: Application): MediaDatabase =
    Room.databaseBuilder(app, MediaDatabase::class.java, "media.db").build()

private fun provideMediaDao(db: MediaDatabase) = db.mediaDao
private fun provideFavoritesDao(db: MediaDatabase) = db.favoritesDao
private fun provideMediaDetailsDao(db: MediaDatabase) = db.mediaDetailsDao
private fun provideRemoteKeysDao(db: MediaDatabase) = db.remoteKeysDao
private fun provideVideosDao(db: MediaDatabase) = db.videosDao
