package com.davidluna.tmdb.media_framework.favorites.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id AND mediaType = :mediaType")
    suspend fun deleteFavorite(id: Int, mediaType: String)

    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE mediaType = :mediaType ORDER BY timestamp DESC")
    fun observeFavoritesByType(mediaType: String): Flow<List<FavoriteEntity>>
}
