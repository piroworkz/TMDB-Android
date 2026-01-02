package com.davidluna.tmdb.media_framework.favorites

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode
import com.davidluna.tmdb.media_domain.favorites.entities.FavoriteItem
import com.davidluna.tmdb.media_domain.favorites.repository.FavoritesRepository
import com.davidluna.tmdb.media_domain.favorites.types.MediaType
import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoritesRepository(
    seed: List<FavoriteItem> = emptyList(),
    private val shouldFail: Boolean = false,
) : FavoritesRepository {
    private val state = MutableStateFlow(seed)

    override fun observeFavorites(): Flow<List<FavoriteItem>> = state.asStateFlow()

    override fun observeFavoritesByType(mediaType: MediaType): Flow<List<FavoriteItem>> =
        state.asStateFlow().map { favorites -> favorites.filter { it.mediaType == mediaType } }

    override suspend fun addFavorite(item: FavoriteItem): Either<AppError, Unit> =
        update { favorites -> favorites + item }

    override suspend fun removeFavorite(id: Int, mediaType: MediaType): Either<AppError, Unit> =
        update { favorites -> favorites.filterNot { it.id == id && it.mediaType == mediaType } }

    override suspend fun toggleFavorite(item: FavoriteItem): Either<AppError, Unit> =
        update { favorites ->
        val exists = favorites.any { it.id == item.id && it.mediaType == item.mediaType }
        if (exists) favorites.filterNot { it.id == item.id && it.mediaType == item.mediaType }
        else favorites + item
    }

    private fun update(
        block: (List<FavoriteItem>) -> List<FavoriteItem>,
    ): Either<AppError, Unit> {
        if (shouldFail) {
            return Either.Left(
                AppError(
                    code = AppErrorCode.LOCAL_ERROR,
                    description = "Fake favorites repository failure"
                )
            )
        }
        state.value = block(state.value)
        return Either.Right(Unit)
    }
}
