package com.davidluna.tmdb.auth_data.di

import android.app.Application
import androidx.room.Room
import com.davidluna.tmdb.auth_data.framework.local.database.AuthenticationDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authFrameworkLocalModule = module {
    singleOf(::provideAuthenticationDatabase)
    singleOf(::provideAccountDao)
    singleOf(::provideSessionDao)
}

private fun provideAuthenticationDatabase(app: Application): AuthenticationDatabase =
    Room.databaseBuilder(app, AuthenticationDatabase::class.java, "authentication.db").build()

private fun provideAccountDao(db: AuthenticationDatabase) = db.accountDao

private fun provideSessionDao(db: AuthenticationDatabase) = db.sessionDao