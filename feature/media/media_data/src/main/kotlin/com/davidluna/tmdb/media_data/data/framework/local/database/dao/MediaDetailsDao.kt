package com.davidluna.tmdb.media_data.data.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.details.RoomMediaDetailsRelations
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.images.RoomImage

@Dao
interface MediaDetailsDao {

    @Transaction
    @Query("SELECT * FROM RoomMediaDetails WHERE id = :id")
    suspend fun getFullDetail(id: Int): RoomMediaDetailsRelations?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: RoomMediaDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCast(cast: List<RoomCast>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<RoomImage>)

    @Query("DELETE FROM RoomMediaDetails WHERE id < :id")
    suspend fun deleteExpiredRoomMediaDetails(id: Int)

    @Transaction
    suspend fun cacheDetails(
        roomMediaDetailsRelations: RoomMediaDetailsRelations,
        isCacheExpired: Boolean,
    ): RoomMediaDetailsRelations? {
        if (isCacheExpired) {
            deleteExpiredRoomMediaDetails(roomMediaDetailsRelations.details.id)
        }
        insertAllRelations(roomMediaDetailsRelations)
        return getFullDetail(roomMediaDetailsRelations.details.id)
    }

    @Transaction
    suspend fun insertAllRelations(details: RoomMediaDetailsRelations) {
        insertDetails(details.details)
        insertCast(details.cast)
        insertImages(details.images)
    }
}