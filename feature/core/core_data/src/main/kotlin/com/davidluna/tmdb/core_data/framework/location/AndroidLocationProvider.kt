package com.davidluna.tmdb.core_data.framework.location

import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AndroidLocationProvider @Inject constructor(
    private val resolver: CountryCodeResolver,
    private val getLocation: LocationService
) : ObserveCountryCode {

    override fun invoke(): Flow<String> = flow {
        val region = getRegionCode()
        emit(region)
    }

    private suspend fun getRegionCode(): String =
        getLocation()?.let(resolver::invoke) ?: CountryCodeResolver.DEFAULT_COUNTRY_CODE
}