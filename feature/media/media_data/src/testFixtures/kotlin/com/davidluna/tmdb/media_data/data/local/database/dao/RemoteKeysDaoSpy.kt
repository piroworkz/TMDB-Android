package com.davidluna.tmdb.media_data.data.local.database.dao

import com.davidluna.tmdb.media_data.framework.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RemoteKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class RemoteKeysDaoSpy : RemoteKeysDao {

    private val inMemoryDatabase: MutableStateFlow<RemoteKeys?> = MutableStateFlow(null)
    private var error: Throwable? = null

    override suspend fun getRemoteKey(category: String): RemoteKeys? {
        tryThrowError()
        return inMemoryDatabase.value
    }

    override suspend fun insertKey(remoteKey: RemoteKeys) {
        tryThrowError()
        inMemoryDatabase.update { remoteKey }
    }

    override suspend fun clearRemoteKey(category: String) {
        tryThrowError()
        inMemoryDatabase.update { null }
    }

    fun setError(error: Throwable) {
        this.error = error
    }

    private fun tryThrowError() {
        error?.let { throw it }
    }
}