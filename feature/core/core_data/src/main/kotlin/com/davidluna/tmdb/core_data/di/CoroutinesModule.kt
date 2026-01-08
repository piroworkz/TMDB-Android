package com.davidluna.tmdb.core_data.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val coroutinesModule = module {
    single { Dispatchers.IO }
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
}