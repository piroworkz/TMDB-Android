package com.davidluna.tmdb.media_data.framework.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia

@Dao
interface MediaDao {
    @Upsert
    suspend fun insertMedia(media: List<RoomMedia>)

    @Query("SELECT * FROM RoomMedia WHERE category = :category")
    fun getMedia(category: String): PagingSource<Int, RoomMedia>

    @Query("DELETE FROM RoomMedia WHERE category = :category")
    suspend fun deleteCatalog(category: String)
}