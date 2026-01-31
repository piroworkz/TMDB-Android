package com.davidluna.tmdb.auth_data.repositories

import arrow.core.getOrElse
import com.davidluna.tmdb.auth_data.framework.local.database.dao.AccountDao
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomUserAccount
import com.davidluna.tmdb.auth_data.framework.remote.UserAccountApi
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteUserAccountDetail
import com.davidluna.tmdb.auth_domain.entities.UserAccount
import com.davidluna.tmdb.auth_domain.usecases.ObserveUserAccount
import com.davidluna.tmdb.core_data.framework.remote.model.buildModel
import com.davidluna.tmdb.core_data.framework.remote.model.toAppError
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepository(
    private val userAccountApi: UserAccountApi,
    private val accountDao: AccountDao
) : ObserveUserAccount, AccountDetailsRepository {

    override val userAccount: Flow<UserAccount?>
        get() = accountDao.getAccount().map { it?.toDomain() }

    override suspend fun fetch(): AppError? = tryCatchSuspend {
        val remoteAccount = userAccountApi.fetchAccountDetails()
            .getOrElse { throw it.toAppError() }
        accountDao.insertAccount(remoteAccount.toLocalStorage())
    }.leftOrNull()

    override suspend fun deleteAccount(): AppError? = tryCatchSuspend {
        accountDao.deleteAccount()
    }.leftOrNull()

    override suspend fun hasAccount(): Boolean = tryCatchSuspend {
        accountDao.hasAccount()
    }.getOrElse { false }

    private fun RemoteUserAccountDetail.toLocalStorage(): RoomUserAccount = RoomUserAccount(
        userId = userId,
        name = name,
        username = username,
        avatarPath = avatar.tmdb.avatarPath.buildModel()
    )

    private fun RoomUserAccount.toDomain(): UserAccount = UserAccount(
        userId = userId,
        name = name,
        username = username,
        avatarPath = avatarPath
    )
}