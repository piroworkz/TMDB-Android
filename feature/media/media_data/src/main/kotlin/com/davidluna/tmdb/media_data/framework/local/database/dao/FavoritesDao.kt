package com.davidluna.tmdb.media_data.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("INSERT INTO RoomFavorite (id, category) VALUES (:id, :category)")
    suspend fun insertFavorite(id: Int, category: String)

    @Query("DELETE FROM RoomFavorite WHERE id = :id AND category = :category")
    suspend fun deleteFavorite(id: Int, category: String)

    @Query("SELECT * FROM RoomFavorite WHERE category = :category")
    fun getFavorites(category: String): Flow<List<RoomFavorite>>

    @Query("SELECT * FROM RoomFavorite WHERE id = :id AND category = :category")
    suspend fun getFavorite(id: Int, category: String): RoomFavorite?

    @Query("DELETE FROM RoomFavorite")
    suspend fun clearFavorites()

    @Transaction()
    suspend fun toggleFavorite(id: Int, category: String) {
        val favorite = getFavorite(id, category)
        if (favorite != null) {
            deleteFavorite(id, category)
        } else {
            insertFavorite(id, category)
        }
    }
}
