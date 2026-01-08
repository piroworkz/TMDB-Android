package com.davidluna.tmdb.auth_ui.di

import com.davidluna.tmdb.auth_ui.presenter.login.LoginViewModel
import com.davidluna.tmdb.auth_ui.presenter.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authPresentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SplashViewModel)
}