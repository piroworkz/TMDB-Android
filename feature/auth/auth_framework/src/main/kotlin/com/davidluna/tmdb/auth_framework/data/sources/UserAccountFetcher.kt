package com.davidluna.tmdb.auth_framework.data.sources

import arrow.core.Either
import com.davidluna.tmdb.auth_domain.usecases.FetchUserAccount
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_framework.data.remote.UserAccountService
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatch
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import javax.inject.Inject

class UserAccountFetcher @Inject constructor(
    private val remote: UserAccountService,
    private val local: AccountDao
) : FetchUserAccount {

    override suspend fun invoke(): Either<AppError, Unit> = tryCatch {
        if (!local.hasAccount()) {
            remote.getAccount().fold(
                ifLeft = { throw it.toAppError() },
                ifRight = { local.insertAccount(it.toLocalStorage()) }
            )
        }
    }
}