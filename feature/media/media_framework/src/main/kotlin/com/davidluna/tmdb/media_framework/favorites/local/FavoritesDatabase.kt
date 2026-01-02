package com.davidluna.tmdb.media_framework.favorites.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteEntity::class],
    exportSchema = true,
    version = 1
)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract val favoritesDao: FavoritesDao
}
