package com.davidluna.tmdb.media_data.framework.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia

@Dao
interface FavoritesDao {

    @Query(
        "SELECT * FROM RoomMedia " +
            "WHERE isFavorite = 1 AND category LIKE :categoryPrefix"
    )
    fun getFavorites(categoryPrefix: String): PagingSource<Int, RoomMedia>

    @Query(
        "UPDATE RoomMedia SET isFavorite = :isFavorite " +
            "WHERE id = :mediaId AND category LIKE :categoryPrefix"
    )
    suspend fun updateFavorite(mediaId: Int, categoryPrefix: String, isFavorite: Boolean)

    @Query("UPDATE RoomMedia SET isFavorite = 0")
    suspend fun clearFavorites()
}
