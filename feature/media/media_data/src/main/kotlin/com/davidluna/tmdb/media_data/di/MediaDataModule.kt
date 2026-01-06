package com.davidluna.tmdb.media_data.di

import com.davidluna.tmdb.media_data.data.framework.local.storage.SelectedCatalogDataSource
import com.davidluna.tmdb.media_data.data.framework.paging.CachePolicyValidator
import com.davidluna.tmdb.media_data.data.framework.paging.IsCacheExpired
import com.davidluna.tmdb.media_data.data.repositories.CatalogVideosRepository
import com.davidluna.tmdb.media_data.data.repositories.MediaCatalogRepository
import com.davidluna.tmdb.media_data.data.repositories.MediaDetailsCacheRepository
import com.davidluna.tmdb.media_domain.usecases.GetCatalogVideos
import com.davidluna.tmdb.media_domain.usecases.GetMediaDetails
import com.davidluna.tmdb.media_domain.usecases.ObserveMediaCatalogUseCase
import com.davidluna.tmdb.media_domain.usecases.ObserveSelectedMediaCatalog
import com.davidluna.tmdb.media_domain.usecases.UpdateSelectedEndpoint
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
}