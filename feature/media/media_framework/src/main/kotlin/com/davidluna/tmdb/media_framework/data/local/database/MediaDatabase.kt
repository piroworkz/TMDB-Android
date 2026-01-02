package com.davidluna.tmdb.media_framework.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.davidluna.tmdb.media_framework.data.local.database.converters.RoomMediaConverters
import com.davidluna.tmdb.media_framework.data.local.database.dao.FavoritesDao
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaDao
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaDetailsDao
import com.davidluna.tmdb.media_framework.data.local.database.dao.MediaVideosDao
import com.davidluna.tmdb.media_framework.data.local.database.dao.RemoteKeysDao
import com.davidluna.tmdb.media_framework.data.local.database.entities.credits.RoomCast
import com.davidluna.tmdb.media_framework.data.local.database.entities.credits.RoomCrew
import com.davidluna.tmdb.media_framework.data.local.database.entities.details.RoomGenre
import com.davidluna.tmdb.media_framework.data.local.database.entities.details.RoomMediaDetails
import com.davidluna.tmdb.media_framework.data.local.database.entities.images.RoomImage
import com.davidluna.tmdb.media_framework.data.local.database.entities.media.RemoteKeys
import com.davidluna.tmdb.media_framework.data.local.database.entities.media.RoomMedia
import com.davidluna.tmdb.media_framework.data.local.database.entities.videos.RoomVideo

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
        RoomFavorite::class,
        RoomVideo::class
    ],
    exportSchema = true,
    version = 2
)
abstract class MediaDatabase : RoomDatabase() {
    abstract val favoritesDao: FavoritesDao
    abstract val mediaDao: MediaDao
    abstract val mediaDetailsDao: MediaDetailsDao
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val videosDao: MediaVideosDao

    companion object {
        val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `RoomFavorite` (" +
                        "`category` TEXT NOT NULL, " +
                        "`id` INTEGER NOT NULL, " +
                        "`posterPath` TEXT NOT NULL, " +
                        "`title` TEXT NOT NULL, " +
                        "PRIMARY KEY(`id`, `category`)" +
                        ")"
                )
            }
        }
    }
}
import com.davidluna.tmdb.media_framework.data.local.database.entities.favorites.RoomFavorite
