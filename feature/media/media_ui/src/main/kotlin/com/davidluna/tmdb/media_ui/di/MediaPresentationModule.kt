package com.davidluna.tmdb.media_ui.di

import com.davidluna.tmdb.media_ui.presenter.detail.MediaDetailsViewModel
import com.davidluna.tmdb.media_ui.presenter.media.MediaCatalogViewModel
import com.davidluna.tmdb.media_ui.presenter.videos.VideoPlayerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mediaPresentationModule = module {
    viewModelOf(::MediaCatalogViewModel)
    viewModelOf(::MediaDetailsViewModel)
    viewModelOf(::VideoPlayerViewModel)
}