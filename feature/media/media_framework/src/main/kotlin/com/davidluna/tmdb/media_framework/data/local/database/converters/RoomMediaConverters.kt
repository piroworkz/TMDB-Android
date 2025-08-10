package com.davidluna.tmdb.media_framework.data.local.database.converters

import androidx.room.TypeConverter
import com.davidluna.tmdb.media_framework.data.local.database.entities.details.RoomGenre
import kotlinx.serialization.json.Json

object RoomMediaConverters {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromGenres(value: List<RoomGenre>): String =
        json.encodeToString(value)

    @TypeConverter
    fun toGenres(value: String): List<RoomGenre> =
        json.decodeFromString(value)
}