package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.Either
import arrow.core.left
import com.davidluna.tmdb.auth_domain.usecases.LoginAsGuest
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_framework.data.remote.RemoteAuthenticationService
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import javax.inject.Inject

class GuestSessionRepository @Inject constructor(
    private val remote: RemoteAuthenticationService,
    private val local: SessionDao
) : LoginAsGuest {

    override suspend fun invoke(): Either<AppError, Unit> = tryCatch {
        remote.createGuestSession().fold(
            ifLeft = { throw it.toAppError() },
            ifRight = { local.insertSession(session = it.toLocalStorage()) }
        )
    }
}