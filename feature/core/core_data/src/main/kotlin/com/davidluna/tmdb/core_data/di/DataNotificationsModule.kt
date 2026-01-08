package com.davidluna.tmdb.core_data.di

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.davidluna.tmdb.core_domain.entities.NotificationDetails
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.coroutines.EmptyCoroutineContext.get

val dataNotificationsModule = module {
    singleOf(::provideNotificationManager)
    singleOf(::provideNotificationManagerCompat)
    singleOf(::providePermissionStatus)
    singleOf(::providesNotification)
}

interface PermissionStatus {
    val value: Boolean
}


fun provideNotificationManager(application: Application): NotificationManager =
    application.getSystemService(NotificationManager::class.java)

fun providePermissionStatus(application: Application): PermissionStatus = object : PermissionStatus {
    override val value: Boolean
        get() = Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
            application,
            POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}


fun provideNotificationManagerCompat(application: Application): NotificationManagerCompat =
    NotificationManagerCompat.from(application)

fun providesNotification(application: Application): NotificationDetails.() -> Notification = {
    NotificationCompat.Builder(application, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setAutoCancel(autocancel)
        .build()
}