package com.davidluna.tmdb.auth_data.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davidluna.tmdb.auth_data.framework.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount

@Database(
    entities = [
        RoomSession::class,
        RoomUserAccount::class
    ],
    exportSchema = true,
    version = 1
)
abstract class AuthenticationDatabase: RoomDatabase() {
    abstract val accountDao: AccountDao
    abstract val sessionDao: SessionDao
}