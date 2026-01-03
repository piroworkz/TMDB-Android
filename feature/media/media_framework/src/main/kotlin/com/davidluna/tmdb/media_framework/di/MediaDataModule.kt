package com.davidluna.tmdb.media_framework.di

import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_domain.usecases.ObserveFavorites
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import com.davidluna.tmdb.media_domain.usecases.ToggleFavorite
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
import com.davidluna.tmdb.media_framework.data.local.storage.LocalFavoritesDataSource
import com.davidluna.tmdb.media_framework.data.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_framework.data.paging.CachePolicyValidator
import com.davidluna.tmdb.media_framework.data.paging.IsCacheExpired
import com.davidluna.tmdb.media_framework.data.repositories.MediaCatalogRepository
import com.davidluna.tmdb.media_framework.data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.media_framework.data.repositories.CatalogVideosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaDataModule {
    @Binds
    abstract fun bindGetMediaCatalogUseCase(source: MediaCatalogRepository): ObserveMediaCatalogUseCase
    @Binds
    abstract fun bindIsCacheExpired(source: CachePolicyValidator): IsCacheExpired
    @Binds
    abstract fun bindGetMediaDetailsUseCase(source: MediaDetailsCacheRepository): GetMediaDetails
    @Binds
    abstract fun bindGetMediaVideosUseCase(source: CatalogVideosRepository): GetCatalogVideos
    @Binds
    abstract fun bindGetContentKindUseCase(source: SelectedCatalogDataSource): ObserveSelectedMediaCatalog
    @Binds
    abstract fun bindUpdateContentKindUseCase(source: SelectedCatalogDataSource): UpdateSelectedEndpoint
    @Binds
    abstract fun bindToggleFavorite(source: LocalFavoritesDataSource): ToggleFavorite
    @Binds
    abstract fun bindObserveFavorites(source: LocalFavoritesDataSource): ObserveFavorites
}
