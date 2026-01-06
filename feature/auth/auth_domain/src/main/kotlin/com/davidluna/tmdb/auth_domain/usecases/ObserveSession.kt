package com.davidluna.tmdb.auth_domain.usecases

import com.davidluna.tmdb.auth_domain.entities.Session
import kotlinx.coroutines.flow.Flow

interface ObserveSession {
    val session: Flow<Session?>
}