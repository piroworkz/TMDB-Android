package com.davidluna.tmdb.media_data.data.local.database.dao

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.davidluna.tmdb.media_data.fakes.fakeRoomMediaList
import com.davidluna.tmdb.media_data.framework.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia

class FavoritesDaoSpy : FavoritesDao {
    private val inMemoryDatabase: MutableList<RoomMedia> = fakeRoomMediaList
        .map { it.copy(isFavorite = it.id.toString().contains("3")) }.toMutableList()
    private var error: Throwable? = null

    override fun getFavorites(categoryPrefix: String): PagingSource<Int, RoomMedia> {
        tryThrow()
        val filteredList =
            inMemoryDatabase.filter { it.isFavorite && it.category.startsWith(categoryPrefix) }
        return object : PagingSource<Int, RoomMedia>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoomMedia> =
                LoadResult.Page(
                    data = filteredList,
                    prevKey = null,
                    nextKey = null
                )

            override fun getRefreshKey(state: PagingState<Int, RoomMedia>): Int? = null
        }

    }

    override suspend fun toggleFavorite(mediaId: Int, categoryPrefix: String) {
        tryThrow()
        val newList = inMemoryDatabase.map { if (it.id == mediaId) it.copy(isFavorite = !it.isFavorite) else it }
        inMemoryDatabase.clear()
        inMemoryDatabase.addAll(newList)
    }

    override suspend fun clearFavorites() {
        inMemoryDatabase.map { it.copy(isFavorite = false) }
    }

    private fun setError() {
        error = IllegalStateException("test exception")
    }

    private fun tryThrow() {
        error?.let { throw it }
        error = null
    }
}