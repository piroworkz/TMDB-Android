package com.davidluna.tmdb.core_data.framework.location

import com.davidluna.tmdb.core_data.framework.location.model.Coordinates

fun interface CountryCodeResolver : (Coordinates) -> String {
    companion object {
        const val DEFAULT_COUNTRY_CODE = "MX"
    }
}