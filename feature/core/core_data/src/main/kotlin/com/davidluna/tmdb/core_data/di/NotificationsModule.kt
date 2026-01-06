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
import com.davidluna.tmdb.core_data.di.qualifiers.PermissionStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {
    @Singleton
    @Provides
    fun provideNotificationManager(application: Application): NotificationManager =
        application.getSystemService(NotificationManager::class.java)

    @Provides
    @PermissionStatus
    fun providePermissionStatus(application: Application): Boolean =
        Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
            application,
            POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    @Provides
    fun provideNotificationManagerCompat(application: Application): NotificationManagerCompat =
        NotificationManagerCompat.from(application)

    @Provides
    fun providesNotification(application: Application): NotificationDetails.() -> Notification = {
        NotificationCompat.Builder(application, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(autocancel)
            .build()
    }
}