package com.davidluna.tmdb.auth_domain.entities

sealed interface LoginMethod {
    data class AuthCredentials(
        val username: String = String(),
        val password: String = String()
    ) : LoginMethod

    data object AsGuest : LoginMethod
}