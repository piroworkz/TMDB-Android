package com.davidluna.tmdb.core_data.framework.location

import com.davidluna.tmdb.core_data.framework.location.model.Coordinates

fun interface LocationService : suspend () -> Coordinates?