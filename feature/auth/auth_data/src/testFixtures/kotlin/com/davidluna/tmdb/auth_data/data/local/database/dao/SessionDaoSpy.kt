package com.davidluna.tmdb.auth_data.data.local.database.dao

import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

class SessionDaoSpy : SessionDao {

    private val inMemoryDataBase: MutableStateFlow<RoomSession?> = MutableStateFlow(null)
    private val shouldThrowException = AtomicBoolean(false)
    private var error: Throwable? = null

    fun shouldThrowException(error: Throwable?) {
        shouldThrowException.set(error != null)
        this.error = error
    }

    override suspend fun insertSession(session: RoomSession): Long {
        tryThrow()
        inMemoryDataBase.update { session }
        return session.id.toLong()
    }

    override fun getSession(): Flow<RoomSession?> = inMemoryDataBase
        .onStart { tryThrow() }

    override suspend fun hasSession(): Boolean {
        tryThrow()
        return inMemoryDataBase.value != null
    }

    override suspend fun deleteSession(): Int {
        tryThrow()
        inMemoryDataBase.update { null }
        return 1
    }

    private fun tryThrow() {
        if (shouldThrowException.get()) {
            throw error!!
        }
    }
}