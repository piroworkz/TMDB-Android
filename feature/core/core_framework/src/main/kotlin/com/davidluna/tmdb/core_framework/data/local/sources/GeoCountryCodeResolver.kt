package com.davidluna.tmdb.core_framework.data.local.sources

import android.location.Geocoder
import android.os.Build
import com.davidluna.tmdb.core_framework.data.local.model.Coordinates
import javax.inject.Inject

class GeoCountryCodeResolver @Inject constructor(
    private val coder: Geocoder
) : CountryCodeResolver {

    override operator fun invoke(coordinates: Coordinates): String = try {
        val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            var countryCode: String? = null
            coder.getFromLocation(coordinates.latitude, coordinates.longitude, 1) {
                countryCode = it.firstOrNull()?.countryCode
            }
            countryCode

        } else {
            @Suppress("DEPRECATION")
            coder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)
                ?.firstOrNull()?.countryCode
        } ?: CountryCodeResolver.DEFAULT_COUNTRY_CODE
        code
    } catch (_: Exception) {
        CountryCodeResolver.DEFAULT_COUNTRY_CODE
    }
}