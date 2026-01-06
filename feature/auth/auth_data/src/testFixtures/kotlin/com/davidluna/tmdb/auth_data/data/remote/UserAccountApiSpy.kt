package com.davidluna.tmdb.auth_data.data.remote

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.davidluna.tmdb.auth_data.framework.remote.UserAccountApi
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteUserAccountDetail
import com.davidluna.tmdb.core_framework.data.remote.model.RemoteError
import com.davidluna.tmdb.test_shared.reader.Reader

class UserAccountApiSpy : UserAccountApi {

    private var shouldThrowError: Boolean = false

    fun throwError(shouldThrow: Boolean) {
        shouldThrowError = shouldThrow
    }

    override suspend fun fetchAccountDetails(): Either<RemoteError, RemoteUserAccountDetail> {
        return if (shouldThrowError) {
            Reader.fromJson<RemoteError>(Reader.REMOTE_ERROR).left()
        } else {
            Reader.fromJson<RemoteUserAccountDetail>(Reader.USER_ACCOUNT).right()
        }
    }
}