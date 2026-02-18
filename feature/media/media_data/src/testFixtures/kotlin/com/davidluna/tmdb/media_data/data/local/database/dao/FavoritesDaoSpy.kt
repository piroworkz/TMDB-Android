package com.davidluna.tmdb.media_data.data.local.database.dao

import com.davidluna.tmdb.media_data.fakes.fakeRoomFavorites
import com.davidluna.tmdb.media_data.framework.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FavoritesDaoSpy : FavoritesDao {
    private val inMemoryDatabase: MutableList<RoomFavorite> = fakeRoomFavorites.toMutableList()
    private var error: Throwable? = null

    override suspend fun insertFavorite(id: Int, category: String) {
        inMemoryDatabase.add(RoomFavorite(id = id, category = category))
    }

    override suspend fun deleteFavorite(id: Int, category: String) {
        inMemoryDatabase.remove(RoomFavorite(id = id, category = category))
    }

    override fun getFavorites(category: String): Flow<List<RoomFavorite>> {
        return flowOf(inMemoryDatabase)
    }

    override suspend fun getFavorite(
        id: Int,
        category: String
    ): RoomFavorite? {
        return inMemoryDatabase.find { it.id == id && it.category == category }
    }

    override suspend fun clearFavorites() {
        inMemoryDatabase.clear()
    }

    fun setError() {
        error = IllegalStateException("test exception")
    }

    fun tryThrow() {
        error?.let { throw it }
        error = null
    }
}