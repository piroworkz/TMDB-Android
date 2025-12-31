package com.davidluna.tmdb.auth_framework.data.sources

import com.davidluna.tmdb.auth_domain.entities.Session
import com.davidluna.tmdb.auth_domain.usecases.ObserveSession
import com.davidluna.tmdb.auth_framework.data.local.database.dao.SessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionStore @Inject constructor(
    private val local: SessionDao
) : ObserveSession {
    override fun invoke(): Flow<Session?> = local.getSession().map { it?.toDomain() }
}