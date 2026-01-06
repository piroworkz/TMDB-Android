package com.davidluna.tmdb.core_data.framework.messaging

import android.app.Application
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import com.davidluna.tmdb.core_domain.usecases.IsChannelEnabled
import javax.inject.Inject

class TmdbNotificationChannelStateReader @Inject constructor(
    application: Application,
) : IsChannelEnabled {
    private val manager =
        ContextCompat.getSystemService(application, NotificationManager::class.java)
    override operator fun invoke(channelId: String): Boolean =
        manager?.getNotificationChannel(channelId)?.importance != NotificationManager.IMPORTANCE_NONE
}