package com.davidluna.tmdb.auth_data.framework.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM RoomUserAccount LIMIT 1")
    fun getAccount(): Flow<RoomUserAccount?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: RoomUserAccount): Long

    @Query("SELECT EXISTS(SELECT 1 FROM RoomUserAccount LIMIT 1)")
    suspend fun hasAccount(): Boolean

    @Query("DELETE FROM RoomUserAccount")
    suspend fun deleteAccount(): Int
}