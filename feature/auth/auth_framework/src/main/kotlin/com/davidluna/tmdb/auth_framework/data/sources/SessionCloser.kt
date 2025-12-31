package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.Either
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import javax.inject.Inject

class SessionCloser @Inject constructor(
    private val accountDao: AccountDao,
    private val sessionDao: SessionDao,
) : CloseSession {
    override suspend operator fun invoke(): Either<AppError, Boolean> = tryCatch {
        val sessionCount = sessionDao.hasSession()
        val hasAccount = accountDao.hasAccount()
        val isSessionDeleted = if (sessionCount) sessionDao.deleteSession() > 0 else true
        val isAccountDeleted = if (hasAccount) accountDao.deleteAccount() > 0 else true
        isSessionDeleted && isAccountDeleted
    }
}