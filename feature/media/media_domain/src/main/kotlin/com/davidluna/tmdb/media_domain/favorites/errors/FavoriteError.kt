package com.davidluna.tmdb.media_domain.favorites.errors

sealed class FavoriteError(val message: String, val cause: Throwable? = null) {
    data object NotFound : FavoriteError("Favorite not found")

    data class Local(val throwable: Throwable?) :
        FavoriteError(throwable?.message ?: "Local error", throwable)
}
