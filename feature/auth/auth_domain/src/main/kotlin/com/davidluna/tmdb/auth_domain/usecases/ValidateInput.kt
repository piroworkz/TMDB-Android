package com.davidluna.tmdb.auth_domain.usecases

import com.davidluna.tmdb.auth_domain.entities.TextInputError
import com.davidluna.tmdb.auth_domain.entities.TextInputType

fun interface ValidateInput : (String?, TextInputType) -> TextInputError?
