package com.davidluna.tmdb.media_data.data.local.database.dao

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia

class MediaDaoSpy : MediaDao {

    private val inMemoryDatabase: MutableList<RoomMedia> = mutableListOf()
    private var error: Throwable? = null

    override suspend fun insertMedia(media: List<RoomMedia>) {
        tryThrow()
        inMemoryDatabase.addAll(media)
    }

    override fun getMedia(category: String): PagingSource<Int, RoomMedia> {
        tryThrow()
        val filteredList = inMemoryDatabase.filter { it.category == category }
        return object : PagingSource<Int, RoomMedia>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RoomMedia> {
                return LoadResult.Page(
                    data = filteredList,
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, RoomMedia>): Int? = null
        }
    }

    override suspend fun deleteCatalog(category: String) {
        tryThrow()
        inMemoryDatabase.removeIf { it.category == category }
    }

    private fun tryThrow() {
        error?.let { throw it }
    }
}