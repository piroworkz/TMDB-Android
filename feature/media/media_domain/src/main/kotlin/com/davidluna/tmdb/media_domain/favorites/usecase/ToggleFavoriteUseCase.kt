package com.davidluna.tmdb.media_domain.favorites.usecase

import arrow.core.Either
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.repository.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) : suspend (FavoriteItem) -> Either<AppError, Unit> {

    override suspend fun invoke(item: FavoriteItem): Either<AppError, Unit> =
        repository.toggleFavorite(item)
}
