package com.davidluna.tmdb.media_data.data.local.database.dao

import com.davidluna.tmdb.media_data.data.framework.local.database.dao.MediaVideosDao
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.videos.RoomVideo

class MediaVideosDaoSpy : MediaVideosDao {

    private val inMemoryDatabase: MutableList<RoomVideo> = mutableListOf()
    private var error: Throwable? = null

    override suspend fun getVideo(mediaId: Int): List<RoomVideo> {
        tryThrow()
        return inMemoryDatabase.filter { it.mediaId == mediaId }
    }

    override suspend fun deleteVideo(mediaId: Int) {
        tryThrow()
        inMemoryDatabase.removeIf { it.mediaId == mediaId }
    }

    override suspend fun insertVideos(videos: List<RoomVideo>) {
        tryThrow()
        inMemoryDatabase.addAll(videos)
    }

    override suspend fun cacheVideos(
        videos: List<RoomVideo>,
        isCacheExpired: Boolean,
    ): List<RoomVideo> {
        if (videos.isEmpty()) {
            return emptyList()
        }
        val mediaId = videos.first().mediaId
        if (isCacheExpired) {
            deleteVideo(mediaId)
        }
        insertVideos(videos)
        return getVideo(mediaId)
    }

    fun setError(error: Throwable) {
        this.error = error
    }

    private fun tryThrow() {
        error?.let { throw it }
    }
}