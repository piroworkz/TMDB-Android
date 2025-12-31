package com.davidluna.tmdb.auth_domain.usecases

import com.davidluna.tmdb.auth_domain.entities.Session
import kotlinx.coroutines.flow.Flow

fun interface ObserveSession : () -> Flow<Session?>