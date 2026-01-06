package com.davidluna.tmdb.media_data.data.local.database.dao

import com.davidluna.tmdb.media_data.data.framework.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.images.RoomImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MediaDetailsDaoSpy : MediaDetailsDao {

    private val inMemoryDatabase: MutableStateFlow<RoomMediaDetailsRelations?> =
        MutableStateFlow(null)
    private var error: Throwable? = null

    override suspend fun getFullDetail(id: Int): RoomMediaDetailsRelations? {
        tryThrow()
        return inMemoryDatabase.value
    }

    override suspend fun insertDetails(details: RoomMediaDetails) {
        tryThrow()
        inMemoryDatabase.update { it?.copy(details = details) }
    }

    override suspend fun insertCast(cast: List<RoomCast>) {
        tryThrow()
        inMemoryDatabase.update { it?.copy(cast = cast) }
    }

    override suspend fun insertImages(images: List<RoomImage>) {
        tryThrow()
        inMemoryDatabase.update { it?.copy(images = images) }
    }

    override suspend fun deleteExpiredRoomMediaDetails(id: Int) {
        tryThrow()
        inMemoryDatabase.update { null }
    }

    override suspend fun cacheDetails(
        roomMediaDetailsRelations: RoomMediaDetailsRelations,
        isCacheExpired: Boolean,
    ): RoomMediaDetailsRelations? {
        tryThrow()
        if (isCacheExpired) {
            deleteExpiredRoomMediaDetails(roomMediaDetailsRelations.details.id)
        }
        insertAllRelations(roomMediaDetailsRelations)
        return getFullDetail(roomMediaDetailsRelations.details.id)
    }

    override suspend fun insertAllRelations(details: RoomMediaDetailsRelations) {
        tryThrow()
        inMemoryDatabase.update { details }
    }

    fun setError(error: Throwable) {
        this.error = error
    }

    private fun tryThrow() {
        error?.let { throw it }
    }
}