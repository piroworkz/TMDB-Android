package com.davidluna.tmdb.core_data.framework.messaging

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.davidluna.tmdb.core_data.di.PermissionStatus
import com.davidluna.tmdb.core_domain.entities.NotificationChannelDetails
import com.davidluna.tmdb.core_domain.entities.NotificationDetails
import com.davidluna.tmdb.core_domain.usecases.ShowNotification
import kotlin.random.Random

class TmdbNotificationsManager(
    private val permissionStatus: PermissionStatus,
    private val manager: NotificationManager?,
    private val compat: NotificationManagerCompat,
    private val map: NotificationDetails.() -> Notification,
) : ShowNotification {

    @SuppressLint("MissingPermission")
    override operator fun invoke(
        notification: NotificationDetails,
        channel: NotificationChannelDetails,
    ): Boolean {
        if (!permissionStatus.value) return false
        ensureChannel(channel)
        compat.notify(Random.nextInt(), map(notification))
        return true
    }

    private fun ensureChannel(channel: NotificationChannelDetails) {
        if (manager?.getNotificationChannel(channel.id) == null) {
            manager?.createNotificationChannel(
                NotificationChannel(channel.id, channel.name, channel.importance).apply {
                    description = channel.description
                }
            )
        }
    }

}