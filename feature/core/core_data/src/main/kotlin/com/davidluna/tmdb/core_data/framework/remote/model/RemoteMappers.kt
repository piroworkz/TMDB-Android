package com.davidluna.tmdb.core_data.framework.remote.model

import com.davidluna.tmdb.core_domain.entities.AppError
import com.davidluna.tmdb.core_domain.entities.AppErrorCode

fun String.buildModel(width: String = "w185"): String =
    "https://image.tmdb.org/t/p/$width$this"

fun RemoteError.toAppError(): AppError = AppError(
    code = AppErrorCode.SERVER,
    description = statusMessage,
    type = null
)