package com.davidluna.tmdb.auth_data.framework.local

import com.davidluna.tmdb.auth_data.framework.local.database.dao.SessionDao
import com.davidluna.tmdb.core_domain.usecases.ObserveCountryCode
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot
import com.davidluna.tmdb.core_data.framework.remote.interceptors.ParametersSnapshot.Keys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryParametersSnapshot @Inject constructor(
    getCountryCode: ObserveCountryCode,
    sessionDao: SessionDao,
    scope: CoroutineScope,
) : ParametersSnapshot {

    private val session = sessionDao.getSession()
        .stateIn(scope, SharingStarted.Eagerly, null)

    private val country = getCountryCode()
        .stateIn(scope, SharingStarted.Eagerly, "US")

    override fun invoke(): Map<String, String> = buildMap {
        val countryCode = country.value
        session.value?.let { put(Keys.SESSION_ID, it.sessionId) }
        put(Keys.REGION, countryCode)
        put(Keys.LANGUAGE, if (countryCode == "MX") "es-mx" else Keys.DEFAULT_LANGUAGE)
        put(Keys.INCLUDE_IMAGE_LANGUAGE, if (countryCode == "MX") "es" else "en")
    }
}