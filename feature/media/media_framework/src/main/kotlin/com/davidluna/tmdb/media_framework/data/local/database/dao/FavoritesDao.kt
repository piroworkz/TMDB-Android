package com.davidluna.tmdb.media_framework.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite

@Dao
interface FavoritesDao {
    @Upsert
    suspend fun upsertFavorite(favorite: RoomFavorite)

    @Query("SELECT * FROM RoomFavorite WHERE id = :id AND category = :category LIMIT 1")
    suspend fun getFavorite(id: Int, category: String): RoomFavorite?

    @Query("DELETE FROM RoomFavorite WHERE id = :id AND category = :category")
    suspend fun deleteFavorite(id: Int, category: String): Int
}
