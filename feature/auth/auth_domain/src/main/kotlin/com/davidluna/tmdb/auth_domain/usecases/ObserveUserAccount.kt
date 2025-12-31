package com.davidluna.tmdb.auth_domain.usecases

import com.davidluna.tmdb.auth_domain.entities.UserAccount
import kotlinx.coroutines.flow.Flow

fun interface ObserveUserAccount: () -> Flow<UserAccount?>