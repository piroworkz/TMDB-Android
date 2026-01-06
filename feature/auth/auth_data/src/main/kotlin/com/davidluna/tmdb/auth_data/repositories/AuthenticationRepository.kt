package com.davidluna.tmdb.auth_data.repositories

import arrow.core.getOrElse
import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.auth_data.framework.local.database.entities.RoomSession
import com.davidluna.tmdb.auth_data.framework.remote.AuthenticationApi
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteGuestSession
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteLoginRequest
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteSessionIdResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteTokenResponse
import com.davidluna.tmdb.auth_data.framework.remote.model.RemoteValidateTokenWithLoginRequest
import com.davidluna.tmdb.auth_domain.entities.LoginMethod
import com.davidluna.tmdb.auth_domain.entities.LoginMethod.AuthCredentials
import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_domain.usecases.CloseSession
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_domain.usecases.OpenSession
import com.davidluna.tmdb.auth_domain.usecases.ValidateSession
import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.tryCatchSuspend
import com.davidluna.tmdb.core_framework.data.remote.model.toAppError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(
    private val authAPI: AuthenticationApi,
    private val sessionDao: SessionDao,
    private val accountDetailsRepository: AccountDetailsRepository
) : OpenSession, CloseSession, ValidateSession, ObserveSession {

    override val session: Flow<Session?>
        get() = sessionDao.getSession().map { it?.toDomain() }

    override suspend fun open(method: LoginMethod): AppError? = if (method is AuthCredentials) {
        withCredentials(method)
    } else {
        asGuest()
    }

    override suspend fun close(): AppError? = tryCatchSuspend {
        val shouldThrow =
            !sessionDao.hasSession() || sessionDao.deleteSession() < 0 || !accountDetailsRepository.isAccountDeleted()
        if (shouldThrow) {
            throw IllegalStateException("Account not deleted or no session found")
        } else {
            null
        }
    }.leftOrNull()

    override suspend fun isValid(): Boolean = tryCatchSuspend {
        val session: RoomSession? = if (sessionDao.hasSession()) {
            sessionDao.getSession().first()
        } else {
            null
        }
        session?.expiresAt?.let {
            val expiresAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(it) ?: Date()
            Date().before(expiresAt)
        } ?: false
    }.getOrNull() == true

    private suspend fun withCredentials(authCredentials: AuthCredentials): AppError? =
        tryCatchSuspend {
            val token = authAPI.createRequestToken().getOrElse { throw it.toAppError() }
            val tokenAuthorization = authAPI.authorizeToken(token.toRemote(authCredentials))
                .getOrElse { throw it.toAppError() }
            val session = authAPI.createSessionId(tokenAuthorization.toLoginRequest())
                .getOrElse { throw it.toAppError() }
            sessionDao.insertSession(session.toLocalStorage())
            accountDetailsRepository.fetch()?.let { throw it }
        }.leftOrNull()

    private suspend fun asGuest(): AppError? = tryCatchSuspend {
        val guestSession = authAPI.createGuestSession().getOrElse { throw it.toAppError() }
        sessionDao.insertSession(guestSession.toLocalStorage()) >= 0
    }.leftOrNull()

    private fun RemoteTokenResponse.toLoginRequest(): RemoteLoginRequest {
        return RemoteLoginRequest(requestToken = requestToken)
    }

    private fun RemoteTokenResponse.toRemote(request: AuthCredentials): RemoteValidateTokenWithLoginRequest =
        RemoteValidateTokenWithLoginRequest(
            requestToken = requestToken,
            username = request.username,
            password = request.password
        )

    private fun RemoteSessionIdResponse.toLocalStorage(): RoomSession = RoomSession(
        sessionId = sessionId,
        isGuest = false,
        expiresAt = null
    )

    private fun RoomSession.toDomain(): Session = Session(
        sessionId = sessionId,
        isGuest = isGuest,
        expiresAt = expiresAt
    )

    private fun RemoteGuestSession.toLocalStorage(): RoomSession = RoomSession(
        sessionId = guestSessionId,
        isGuest = success,
        expiresAt = expiresAt
    )
}