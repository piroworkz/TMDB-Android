package com.davidluna.tmdb.media_data.data.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidluna.tmdb.media_data.data.framework.local.database.entities.media.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Query("SELECT * FROM RemoteKeys WHERE category = :category")
    suspend fun getRemoteKey(category: String): RemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(remoteKey: RemoteKeys)

    @Query("DELETE FROM RemoteKeys WHERE category = :category")
    suspend fun clearRemoteKey(category: String)
}
