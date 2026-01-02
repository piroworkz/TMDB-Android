package com.davidluna.tmdb.media_framework.data.local.database.dao

import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite

class FavoritesDaoSpy : FavoritesDao {

    private val inMemoryDatabase: MutableList<RoomFavorite> = mutableListOf()
    private var error: Throwable? = null

    override suspend fun upsertFavorite(favorite: RoomFavorite) {
        tryThrow()
        inMemoryDatabase.removeIf { it.id == favorite.id && it.category == favorite.category }
        inMemoryDatabase.add(favorite)
    }

    override suspend fun getFavorite(id: Int, category: String): RoomFavorite? {
        tryThrow()
        return inMemoryDatabase.firstOrNull { it.id == id && it.category == category }
    }

    override suspend fun deleteFavorite(id: Int, category: String): Int {
        tryThrow()
        return if (inMemoryDatabase.removeIf { it.id == id && it.category == category }) 1 else 0
    }

    fun setError(error: Throwable) {
        this.error = error
    }

    private fun tryThrow() {
        error?.let { throw it }
    }
}
