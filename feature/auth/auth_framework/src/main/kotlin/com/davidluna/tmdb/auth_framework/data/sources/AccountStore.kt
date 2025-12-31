package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.auth_framework.data.local.database.dao.AccountDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountStore @Inject constructor(
    private val local: AccountDao
) : ObserveUserAccount {
    override fun invoke(): Flow<UserAccount?> = local.getAccount().map { it?.toDomain() }
}