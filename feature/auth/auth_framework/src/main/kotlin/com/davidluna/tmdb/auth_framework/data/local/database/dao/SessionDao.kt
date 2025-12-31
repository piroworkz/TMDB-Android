package com.davidluna.tmdb.auth_framework.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RoomSession): Long

    @Query("SELECT * FROM RoomSession LIMIT 1")
    fun getSession(): Flow<RoomSession?>

    @Query("SELECT EXISTS (SELECT 1 FROM RoomSession LIMIT 1)")
    suspend fun hasSession(): Boolean

    @Query("DELETE FROM RoomSession")
    suspend fun deleteSession(): Int
}