package com.davidluna.tmdb.auth_framework.data.local.database.dao

import com.davidluna.tmdb.auth_framework.data.local.database.entities.RoomUserAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

class AccountDaoSpy : AccountDao {

    private val inMemoryDatabase: MutableStateFlow<RoomUserAccount?> = MutableStateFlow(null)
    private val shouldThrowException = AtomicBoolean(false)
    private var error: Throwable? = null

    override suspend fun insertAccount(account: RoomUserAccount): Long {
        tryThrow()
        inMemoryDatabase.update { account }
        return account.userId.toLong()
    }

    override fun getAccount(): Flow<RoomUserAccount?> = inMemoryDatabase
        .onStart { tryThrow() }

    override suspend fun hasAccount(): Boolean {
        tryThrow()
        return inMemoryDatabase.value != null
    }

    override suspend fun deleteAccount(): Int {
        tryThrow()
        inMemoryDatabase.update { null }
        return 1
    }

    fun shouldThrowException(error: Throwable?) {
        shouldThrowException.set(error != null)
        this.error = error
    }

    private fun tryThrow() {
        if (shouldThrowException.get()) {
            throw error!!
        }
    }
}
