package com.davidluna.tmdb.media_data.framework.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidluna.tmdb.media_data.framework.local.database.converters.RoomMediaConverters
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDao
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_data.framework.local.database.dao.MediaVideosDao
import com.davidluna.tmdb.media_data.framework.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_data.framework.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_data.framework.local.database.entities.credits.RoomCrew
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomGenre
import com.davidluna.tmdb.media_data.framework.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_data.framework.local.database.entities.images.RoomImage
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RemoteKeys
import com.davidluna.tmdb.media_data.framework.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_data.framework.local.database.entities.videos.RoomVideo

@TypeConverters(RoomMediaConverters::class)
@Database(
    entities = [
        RemoteKeys::class,
        RoomCast::class,
        RoomCrew::class,
        RoomGenre::class,
        RoomImage::class,
        RoomMedia::class,
        RoomMediaDetails::class,
        RoomVideo::class
    ],
    exportSchema = true,
    version = 1
)
abstract class MediaDatabase : RoomDatabase() {
    abstract val mediaDao: MediaDao
    abstract val mediaDetailsDao: MediaDetailsDao
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val videosDao: MediaVideosDao
}