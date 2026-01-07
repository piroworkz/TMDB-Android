package com.davidluna.tmdb.media_data.framework.paging

import java.lang.System.currentTimeMillis
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class CachePolicyValidator @Inject constructor() : IsCacheExpired {
    override fun invoke(lastUpdated: Long?): Boolean =
        lastUpdated == null || currentTimeMillis() - lastUpdated > 7.days.inWholeMilliseconds
}